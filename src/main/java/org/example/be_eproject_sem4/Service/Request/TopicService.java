package org.example.be_eproject_sem4.Service.Request;

import jakarta.transaction.Transactional;
import org.example.be_eproject_sem4.Dto.TopicDTO;
import org.example.be_eproject_sem4.Entity.Topic;
import org.example.be_eproject_sem4.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    // 1. Lấy danh sách tất cả các Topic
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    // 2. Lấy chi tiết một Topic theo ID
    public Topic getTopicById(Integer id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic với ID: " + id));
    }

    // 3. Tạo mới một Topic
    @Transactional
    public Topic createTopic(TopicDTO dto) {
        Topic topic = new Topic();
        topic.setName(dto.getName());
        topic.setNumCards(dto.getNumCards());
        topic.setDescription(dto.getDescription());

        return topicRepository.save(topic);
    }

    // 4. Cập nhật Topic đã tồn tại
    @Transactional
    public Topic updateTopic(Integer id, TopicDTO dto) {
        // Kiểm tra xem topic có tồn tại hay không trước khi update
        Topic existingTopic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không thể cập nhật. Topic ID " + id + " không tồn tại"));

        // Cập nhật các trường thông tin
        existingTopic.setName(dto.getName());
        existingTopic.setNumCards(dto.getNumCards());
        existingTopic.setDescription(dto.getDescription());

        return topicRepository.save(existingTopic);
    }

    // 5. Xóa Topic
    @Transactional
    public void deleteTopic(Integer id) {
        if (!topicRepository.existsById(id)) {
            throw new RuntimeException("Không thể xóa. Topic ID " + id + " không tồn tại");
        }
        topicRepository.deleteById(id);
    }

    // 6. Tìm kiếm Topic theo tên (Optional - mở rộng thêm)
    public List<Topic> searchByName(String name) {
        // Giả sử bạn đã thêm method findByNameContaining trong Repository
        // return topicRepository.findByNameContaining(name);
        return null;
    }
}
