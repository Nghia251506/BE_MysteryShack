package org.example.be_eproject_sem4.Service.History;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.be_eproject_sem4.Repository.InterpretationFormRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ReadingSessionRepository sessionRepository;
    private final InterpretationFormRepository interpretationFormRepository;

    // =========================================================================
    // 1. PHẦN CỦA KHÁCH HÀNG (CUSTOMER)
    // =========================================================================

    /**
     * Khách hàng tạo một yêu cầu trải bài mới
     */
    @Transactional
    public History createRequest(Long customerId, Long questionId, Long sessionId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        TopicQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        History history = History.builder()
                .customer(customer)
                .question(question)
                .request(session)
                .status(ReadingStatus.PENDING) // Mặc định là chờ
                .reader(null) // Chưa có ai nhận
                .createdAt(LocalDateTime.now())
                .build();

        return historyRepository.save(history);
    }

    /**
     * Khách hàng gửi đánh giá sau khi đã có kết quả
     */
    @Transactional
    public History submitFeedback(Long historyId, Long customerId, Integer rating, String feedback) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        // Validate: Phải đúng khách hàng đó và đơn phải xong rồi mới được đánh giá
        if (!history.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn hàng này");
        }
        if (history.getStatus() != ReadingStatus.COMPLETED) {
            throw new RuntimeException("Đơn hàng chưa hoàn thành, không thể đánh giá");
        }

        history.setRating(rating);
        history.setFeedback(feedback);

        return historyRepository.save(history);
    }

    // Lấy lịch sử của khách hàng
    public List<History> getCustomerHistory(Long customerId) {
        return historyRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    // =========================================================================
    // 2. PHẦN CỦA READER (NGƯỜI ĐỌC BÀI)
    // =========================================================================

    /**
     * Reader xem danh sách các đơn "Vô chủ" để nhận việc
     */
    public List<History> getAvailableJobs() {
        return historyRepository.findByReaderIsNullAndStatus(ReadingStatus.PENDING);
    }

    /**
     * Reader nhận một đơn hàng về làm (Assign Job)
     */
    @Transactional
    public History acceptRequest(Long historyId, Long readerId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        User reader = userRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Reader not found"));

        // Validate: Đơn này đã có ai nhận chưa?
        if (history.getReader() != null) {
            throw new RuntimeException("Đơn hàng này đã được Reader khác nhận rồi!");
        }

        history.setReader(reader);
        history.setStatus(ReadingStatus.ACCEPTED); // Chuyển sang đang xử lý

        return historyRepository.save(history);
    }

    /**
     * Reader nộp kết quả trả lời (Submit Result)
     */
    @Transactional
    public History submitResult(Long historyId, Long readerId, InterpretationForm formInput) {
        // 1. Tìm History
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        // 2. Validate Reader
        if (history.getReader() == null || !history.getReader().getId().equals(readerId)) {
            throw new RuntimeException("Bạn không phải là người xử lý đơn hàng này");
        }

        // 3. Tạo và Lưu InterpretationForm
        InterpretationForm form = InterpretationForm.builder()
                .history(history) // Gắn vào History
                .interpretation1(formInput.getInterpretation1())
                .interpretation2(formInput.getInterpretation2())
                .interpretation3(formInput.getInterpretation3())
                .advice(formInput.getAdvice())
                .qrPayment(formInput.getQrPayment())
                .status(InterpretationStatus.COMPLETED) // Giả sử đã xong
                .build();

        interpretationFormRepository.save(form);

        // 4. Update History
        // Không cần setResult string nữa, vì đã có relation
        history.setStatus(ReadingStatus.COMPLETED);
        history.setCompletedAt(LocalDateTime.now());

        return historyRepository.save(history);
    }

    // Lấy danh sách việc riêng của Reader
    public List<History> getReaderJobs(Long readerId) {
        return historyRepository.findByReaderIdOrderByCreatedAtDesc(readerId);
    }
}
