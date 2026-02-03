package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.TestReader.QuestionResponseDTO;
import org.example.be_eproject_sem4.Dto.TestReader.TestResultDTO;
import org.example.be_eproject_sem4.Dto.TestReader.TestSubmitDTO;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.example.be_eproject_sem4.Service.TestReader.ReaderTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest; // Import này quan trọng
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/reader-test")
@RequiredArgsConstructor
public class ReaderTestController {

    private final ReaderTestService testService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestions() {
        return ResponseEntity.ok(testService.getTestForReader());
    }

    @PostMapping("/submit")
    public ResponseEntity<TestResultDTO> submit(
            @RequestBody TestSubmitDTO submitDTO,
            HttpServletRequest request // Lấy request để móc token
    ) {
        // 1. Lấy chuỗi "Bearer token..." từ header
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // 2. Cắt bỏ 7 ký tự đầu ("Bearer ") để lấy token nguyên bản
            String token = bearerToken.substring(7);

            // 3. Truyền token vào hàm của ông
            Long currentUserId = jwtTokenProvider.getUserIdFromToken(token);

            if (currentUserId != null) {
                return ResponseEntity.ok(testService.submitTest(currentUserId, submitDTO));
            }
        }

        // Trả về 401 nếu không có token hoặc token lỏ
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}