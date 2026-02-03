package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.TestReader.ApprovalRequestDTO;
import org.example.be_eproject_sem4.Entity.ReaderApplication;
import org.example.be_eproject_sem4.Service.TestReader.AdminApprovalService;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/approvals")
@RequiredArgsConstructor
public class AdminApprovalController {

    private final AdminApprovalService approvalService;

    // Xem danh sách chờ
    @GetMapping("/pending")
    public ResponseEntity<List<ReaderApplication>> getPending() {
        return ResponseEntity.ok(approvalService.getPendingApplications());
    }

    // Chấp nhận
    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody ApprovalRequestDTO request) {
        approvalService.approveReader(request);
        return ResponseEntity.ok("Đã duyệt Reader thành công!");
    }

    // Từ chối
    @PostMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody ApprovalRequestDTO request) {
        approvalService.rejectReader(request);
        return ResponseEntity.ok("Đã từ chối hồ sơ.");
    }
}
