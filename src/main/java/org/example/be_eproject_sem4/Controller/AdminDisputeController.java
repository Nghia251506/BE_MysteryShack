package org.example.be_eproject_sem4.Controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.ReadingDispute.CreateDisputeRequest;
import org.example.be_eproject_sem4.Dto.ReadingDispute.DisputeResponse;
import org.example.be_eproject_sem4.Dto.ReadingDispute.ResolveDisputeRequest;
import org.example.be_eproject_sem4.Service.ReadingDisputeService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/disputes")
@RequiredArgsConstructor
public class AdminDisputeController {

    private final ReadingDisputeService disputeService;

    @GetMapping
    public ResponseEntity<Page<DisputeResponse>> getAllDisputes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(disputeService.getAllDisputes(status, keyword, pageable));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<DisputeResponse> resolveDispute(
            @PathVariable Long id,
            @RequestBody ResolveDisputeRequest request) {
        return ResponseEntity.ok(disputeService.resolveDispute(id, request));
    }
}