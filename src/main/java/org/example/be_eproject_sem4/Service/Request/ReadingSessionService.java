package org.example.be_eproject_sem4.Service.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.example.be_eproject_sem4.Dto.ReadingSessionDTO;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.Topic;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class ReadingSessionService {

    @Autowired
    private ReadingSessionRepository sessionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. Lấy tất cả các phiên đọc
    public List<ReadingSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    // 2. Lấy chi tiết phiên đọc theo ID
    public ReadingSession getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên đọc ID: " + id));
    }

    // 3. Tạo mới một phiên đọc
    @Transactional
    public ReadingSession createSession(ReadingSessionDTO dto) {
        ReadingSession session = new ReadingSession();

        // Liên kết Topic
        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic ID " + dto.getTopicId() + " không tồn tại"));

        session.setCustomerId(dto.getCustomerId());
        session.setQuestionId(dto.getQuestionId());
        session.setCustomerQuestion(dto.getCustomerQuestion());
        session.setSummaryMeaning(dto.getSummaryMeaning());
        session.setStatus(dto.getStatus() != null ? dto.getStatus().name() : "PENDING");

        // Xử lý chuyển đổi List cards sang chuỗi JSON để lưu vào DB
        if (dto.getSelectedCards() != null) {
            String jsonStr = objectMapper.writeValueAsString(dto.getSelectedCards());
            session.setSelectedCards(jsonStr);
        }

        return sessionRepository.save(session);
    }

    // 4. Cập nhật phiên đọc (Thường dùng để cập nhật kết quả sau khi trải bài)
    @Transactional
    public ReadingSession updateSession(Long id, ReadingSessionDTO dto) {
        ReadingSession existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên đọc để cập nhật"));

        existingSession.setSummaryMeaning(dto.getSummaryMeaning());
        existingSession.setStatus(dto.getStatus().name());

        if (dto.getSelectedCards() != null) {
            String jsonStr = objectMapper.writeValueAsString(dto.getSelectedCards());
            existingSession.setSelectedCards(jsonStr);
        }

        return sessionRepository.save(existingSession);
    }

    // 5. Xóa phiên đọc
    @Transactional
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new RuntimeException("Phiên đọc không tồn tại");
        }
        sessionRepository.deleteById(id);
    }
}
