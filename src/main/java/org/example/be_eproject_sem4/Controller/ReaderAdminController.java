package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.Admin.AdjustEloRequestDto;
import org.example.be_eproject_sem4.Service.Admin.ReaderAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/readers")
@RequiredArgsConstructor
public class ReaderAdminController {
    private final ReaderAdminService readerAdminService;

    @PostMapping("/{id}/adjust-elo")
    public ResponseEntity<?> adjustElo(@PathVariable Long id, @RequestBody AdjustEloRequestDto dto) {
        String adminName = SecurityContextHolder.getContext().getAuthentication().getName();
        readerAdminService.adjustElo(id, dto, adminName);
        return ResponseEntity.ok("Điều chỉnh ELO thành công");
    }

    // @PatchMapping("/{id}/status")
    // public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateReaderStatusDto dto) {
    //     readerAdminService.updateStatus(id, dto);
    //     return ResponseEntity.ok("Cập nhật trạng thái thành công");
    // }
}
