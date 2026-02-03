package org.example.be_eproject_sem4.Controller;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.TestReader.TestBankDTO;
import org.example.be_eproject_sem4.Entity.TestBank;
import org.example.be_eproject_sem4.Service.TestReader.TestBankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/test-bank")
@RequiredArgsConstructor
public class AdminTestBankController {
    private final TestBankService service;

    @GetMapping
    public ResponseEntity<List<TestBank>> getAll() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @PostMapping
    public ResponseEntity<TestBank> create(@RequestBody TestBankDTO dto) {
        return ResponseEntity.ok(service.createQuestion(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestBank> update(@PathVariable Long id, @RequestBody TestBankDTO dto) {
        return ResponseEntity.ok(service.updateQuestion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
