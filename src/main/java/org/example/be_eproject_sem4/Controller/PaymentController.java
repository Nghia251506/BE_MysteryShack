package org.example.be_eproject_sem4.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.VipPackage;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Repository.VipPackageRepository;
import org.example.be_eproject_sem4.Service.VipPackage.SubscriptionService;
import org.example.be_eproject_sem4.Service.Vnpay.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VNPayService vnpayService;
    private final VipPackageRepository vipPackageRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @GetMapping("/vnpay/create")
    public ResponseEntity<?> createUrl(@RequestParam Integer packageId, HttpServletRequest request) {
        // 1. Lấy Reader đang login
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Bạn cần đăng nhập!"));

        // 2. Lấy thông tin gói
        VipPackage pkg = vipPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Gói không tồn tại"));

        // 3. Tạo link
        String paymentUrl = vnpayService.createPaymentUrl(request,
                pkg.getPrice().longValue(),
                packageId,
                currentReader.getId());

        return ResponseEntity.ok(Map.of("url", paymentUrl));
    }

    @PostMapping("/buy-vip")
    public ResponseEntity<?> buyVipPackage(@RequestParam Integer packageId, HttpServletRequest request) {
        // 1. Lấy thông tin Reader đang login (Style của ông)
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Bạn cần đăng nhập để thực hiện giao dịch!"));

        // 2. Kiểm tra gói có tồn tại không
        VipPackage pkg = vipPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Gói VIP không hợp lệ!"));

        // 3. Tạo URL thanh toán VNPay
        // Lưu ý: Tôi truyền thêm packageId và readerId vào OrderInfo để lát nữa Callback mình biết nạp cho ai
        String paymentUrl = vnpayService.createPaymentUrl(
                request,
                pkg.getPrice().longValue(),
                packageId,
                currentReader.getId()
        );

        // 4. Trả về URL cho FE để FE điều hướng Reader sang trang VNPay
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<?> handleVNPayCallback(HttpServletRequest request) {
        // 1. Lấy toàn bộ tham số VNPay gửi về
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            fields.put(fieldName, request.getParameter(fieldName));
        }

        // 2. Kiểm tra chữ ký bảo mật (Sử dụng hàm verify của vnpayService)
        boolean isValidSignature = vnpayService.verifyCallback(fields);

        if (isValidSignature) {
            String responseCode = fields.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                // THANH TOÁN THÀNH CÔNG
                // Parse OrderInfo để lấy PackageId và ReaderId
                // Chuỗi của ông là: "PAY_VIP_PKG_" + packageId + "_USER_" + readerId
                String orderInfo = fields.get("vnp_OrderInfo");
                try {
                    String[] parts = orderInfo.split("_");
                    Integer packageId = Integer.parseInt(parts[3]);
                    Long readerId = Long.parseLong(parts[5]);

                    // Gọi Service kích hoạt gói VIP cho Reader
                    subscriptionService.activateSubscriptionByReaderId(readerId, packageId);

                    return ResponseEntity.ok(Map.of(
                            "status", "00",
                            "message", "Kích hoạt gói VIP thành công!"
                    ));
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("Lỗi xử lý dữ liệu đơn hàng");
                }
            } else {
                // Thanh toán thất bại hoặc người dùng hủy
                return ResponseEntity.ok(Map.of(
                        "status", responseCode,
                        "message", "Thanh toán không thành công"
                ));
            }
        } else {
            return ResponseEntity.badRequest().body("Chữ ký VNPay không hợp lệ!");
        }
    }
}
