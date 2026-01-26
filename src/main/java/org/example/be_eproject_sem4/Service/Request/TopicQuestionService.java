package org.example.be_eproject_sem4.Service.Request;


import jakarta.transaction.Transactional;
import org.example.be_eproject_sem4.Dto.QuestionDTO;
import org.example.be_eproject_sem4.Entity.Topic;
import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.example.be_eproject_sem4.Repository.QuestionRepository;
import org.example.be_eproject_sem4.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    // 1. Lấy tất cả câu hỏi
    public List<TopicQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    // 2. Lấy danh sách câu hỏi theo Topic ID (Nghiệp vụ thường dùng nhất)
    public List<TopicQuestion> getQuestionsByTopicId(Integer topicId) {
        // Giả sử bạn thêm method này vào QuestionRepository: List<TopicQuestion> findByTopicId(Integer topicId);
        return questionRepository.findAll().stream()
                .filter(q -> q.getTopic().getId().equals(topicId))
                .toList();
    }

    // 3. Lấy chi tiết một câu hỏi
    public TopicQuestion getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
    }

    // 4. Tạo mới câu hỏi (Cần check Topic tồn tại)
    @Transactional
    public TopicQuestion createQuestion(QuestionDTO dto) {
        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic ID: " + dto.getTopicId()));

        TopicQuestion question = new TopicQuestion();
        question.setTopic(topic);
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionTextEn(dto.getQuestionTextEn());
        question.setIsPopular(dto.getIsPopular());

        return questionRepository.save(question);
    }

    // 5. Cập nhật câu hỏi
    @Transactional
    public TopicQuestion updateQuestion(Long id, QuestionDTO dto) {
        TopicQuestion existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi để cập nhật"));

        // Nếu thay đổi topic của câu hỏi
        if (!existingQuestion.getTopic().getId().equals(dto.getTopicId())) {
            Topic newTopic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic mới không tồn tại"));
            existingQuestion.setTopic(newTopic);
        }

        existingQuestion.setQuestionText(dto.getQuestionText());
        existingQuestion.setQuestionTextEn(dto.getQuestionTextEn());
        existingQuestion.setIsPopular(dto.getIsPopular());

        return questionRepository.save(existingQuestion);
    }

    // 6. Xóa câu hỏi
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Câu hỏi không tồn tại để xóa");
        }
        questionRepository.deleteById(id);
    }
}