package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.QuestionDTO;
import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.example.be_eproject_sem4.Service.Request.TopicQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@CrossOrigin(origins = "*")
public class TopicQuestionController {

    @Autowired
    private TopicQuestionService questionService;

    // 1. Lấy tất cả câu hỏi trong hệ thống
    @GetMapping
    public ResponseEntity<List<TopicQuestion>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // 2. Lấy danh sách câu hỏi thuộc một Topic cụ thể
    // Ví dụ: GET /api/v1/questions/topic/1
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<TopicQuestion>> getQuestionsByTopic(@PathVariable Integer topicId) {
        return ResponseEntity.ok(questionService.getQuestionsByTopicId(topicId));
    }

    // 3. Lấy chi tiết một câu hỏi theo ID
    @GetMapping("/{id}")
    public ResponseEntity<TopicQuestion> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    // 4. Tạo mới câu hỏi
    @PostMapping
    public ResponseEntity<TopicQuestion> createQuestion(@RequestBody QuestionDTO questionDTO) {
        TopicQuestion createdQuestion = questionService.createQuestion(questionDTO);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    // 5. Cập nhật câu hỏi
    @PutMapping("/{id}")
    public ResponseEntity<TopicQuestion> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDTO));
    }

    // 6. Xóa câu hỏi
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
