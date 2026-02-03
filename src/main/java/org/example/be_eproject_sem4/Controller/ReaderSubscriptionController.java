package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Repository.SubscriptionRepository;
import org.example.be_eproject_sem4.Service.VipPackage.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reader/")
@RequiredArgsConstructor
public class ReaderSubscriptionController {
    private final SubscriptionService subscriptionService;
    @GetMapping("/payment-return")
    public ResponseEntity<?> handleReturn(@RequestParam("vnp_ResponseCode") String responseCode,
                                          @RequestParam("packageId") Integer packageId) {
        if ("00".equals(responseCode)) {
            // Gọi service lấy user từ SecurityContext và nạp gói
            subscriptionService.activateSubscription(packageId);
            return ResponseEntity.ok("Nâng cấp VIP thành công!");
        }
        return ResponseEntity.badRequest().body("Thanh toán thất bại");
    }
}
