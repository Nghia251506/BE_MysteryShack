package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.ReadingDispute.CreateDisputeRequest;
import org.example.be_eproject_sem4.Dto.ReadingDispute.DisputeResponse;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.ReadingDisputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final ReadingDisputeService disputeService;

    @PostMapping
    public ResponseEntity<DisputeResponse> createDispute(
            @RequestBody CreateDisputeRequest request,
            @AuthenticationPrincipal User currentUser) { // Lấy User từ Token
        return ResponseEntity.ok(disputeService.createDispute(request, currentUser));
    }
}