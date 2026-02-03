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
    public ResponseEntity<TestResultDTO> submit(@RequestBody TestSubmitDTO submitDTO) {
        return ResponseEntity.ok(testService.submitTest( submitDTO));
    }
}