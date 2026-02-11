package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.VipPackage.SubscriptionAdminResponse;
import org.example.be_eproject_sem4.Dto.VipPackage.SubscriptionResponse;
import org.example.be_eproject_sem4.Service.VipPackage.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/current")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription() {
        SubscriptionResponse current = subscriptionService.getCurrentSubscription();
        if (current == null) {
            return ResponseEntity.ok(null); // Trả về 200 OK nhưng body trống
        }
        return ResponseEntity.ok(current);
    }
}
