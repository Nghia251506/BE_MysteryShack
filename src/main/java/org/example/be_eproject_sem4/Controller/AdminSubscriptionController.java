package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.VipPackage.SubscriptionAdminResponse;
import org.example.be_eproject_sem4.Service.VipPackage.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscriptions")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<SubscriptionAdminResponse>> getAll() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptionsForAdmin());
    }
}
