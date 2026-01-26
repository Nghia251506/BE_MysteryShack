package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.TopicDTO;
import org.example.be_eproject_sem4.Entity.Topic;
import org.example.be_eproject_sem4.Service.Request.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API mà không bị lỗi CORS
public class TopicController {

    @Autowired
    private TopicService topicService;

    // 1. Lấy tất cả danh sách chủ đề
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    // 2. Lấy chi tiết một chủ đề theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Integer id) {
        Topic topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    // 3. Tạo mới một chủ đề
    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody TopicDTO topicDTO) {
        Topic createdTopic = topicService.createTopic(topicDTO);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    // 4. Cập nhật một chủ đề
    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(
            @PathVariable Integer id,
            @RequestBody TopicDTO topicDTO) {
        Topic updatedTopic = topicService.updateTopic(id, topicDTO);
        return ResponseEntity.ok(updatedTopic);
    }

    // 5. Xóa một chủ đề
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTopic(@PathVariable Integer id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok("Xóa thành công Topic có ID: " + id);
    }
}
