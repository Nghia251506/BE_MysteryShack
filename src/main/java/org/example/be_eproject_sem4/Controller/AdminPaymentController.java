package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Service.Payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@Tag(name = "Admin Payment Management", description = "Endpoints dành cho Admin quản lý dòng tiền")
public class AdminPaymentController {

    private final PaymentService paymentService;

    // 1. Lấy toàn bộ danh sách giao dịch (Có phân trang để tránh lag khi data lớn)
    @GetMapping
    public ResponseEntity<?> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paymentService.getAllPayments(page, size));
    }

    // 2. Lấy chi tiết một giao dịch (Để Admin check mã VNPay, Ngân hàng...)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentDetail(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentDetail(id));
    }

    // 3. API Thống kê nhanh (Dùng cho mấy cái Card ở Dashboard Admin)
    @GetMapping("/stats")
    public ResponseEntity<?> getRevenueStats() {
        return ResponseEntity.ok(paymentService.getRevenueStats());
    }

    // 4. (Tùy chọn) Chỉnh sửa trạng thái thủ công - Đề phòng lỗi hệ thống
    // @PatchMapping("/{id}/status")
    // public ResponseEntity<?> updatePaymentStatus(
    //         @PathVariable Long id, 
    //         @RequestParam String status) {
    //     paymentService.updateStatusManually(id, status);
    //     return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    // }
}
