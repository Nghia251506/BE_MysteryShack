package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.ReaderProfile.*;
import org.example.be_eproject_sem4.Service.ReaderProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reader-profiles")
@RequiredArgsConstructor
@CrossOrigin("*") // Để FE gọi thoải mái
public class ReaderProfileController {

    private final ReaderProfileService readerProfileService;

    @GetMapping("/{readerId}")
    public ResponseEntity<ReaderProfileDTO> getProfile(@PathVariable Long readerId) {
        return ResponseEntity.ok(readerProfileService.getUnifiedReaderProfile(readerId));
    }
}