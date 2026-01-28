package org.example.be_eproject_sem4.Service.Interpretation;

import org.example.be_eproject_sem4.Dto.InterpretationResponseDto;
import org.example.be_eproject_sem4.Dto.InterpretationSubmitDto;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Mapper.InterpretationMapper;
import org.example.be_eproject_sem4.Repository.HistoryRepository;
import org.example.be_eproject_sem4.Repository.InterpretationFormRepository;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterpretationService {

    private final InterpretationFormRepository formRepository;
    private final ReadingSessionRepository sessionRepository;
    private final InterpretationMapper interpretationMapper;
    private final HistoryRepository historyRepository;

    /**
     * Reader nộp bài luận giải cho 3 lá bài kèm lời khuyên và QR
     */
    @Transactional
    public InterpretationResponseDto submitInterpretation(Long sessionId, InterpretationSubmitDto dto) {
        // 1. Tìm và kiểm tra Session
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng (Session)"));

        if (!"ACCEPTED".equals(session.getStatus())) {
            throw new RuntimeException("Đơn hàng phải ở trạng thái ACCEPTED mới có thể nộp bài.");
        }

        // 2. Tạo Form luận giải mới (Logic cũ của bạn)
        InterpretationForm form = new InterpretationForm();
        form.setRequestId(session);
        form.setInterpretation1(dto.getInterpretation1());
        form.setInterpretation2(dto.getInterpretation2());
        form.setInterpretation3(dto.getInterpretation3());
        form.setAdvice(dto.getAdvice());
        form.setQrPayment(dto.getQrPayment());

        if (dto.getInterpretation1() == null || dto.getInterpretation1().trim().isEmpty()) {
            throw new RuntimeException("Nội dung luận giải lá bài 1 không được để trống.");
        }

        // Cập nhật trạng thái Form và Session
        form.setStatus(InterpretationStatus.SENT_TO_CUSTOMER);
        session.setStatus("INTERPRETED"); // Session chuyển sang trạng thái chờ thanh toán

        // Lưu Form
        InterpretationForm savedForm = formRepository.save(form);

        // ==================================================================
        // 3. LOGIC MỚI: CẬP NHẬT HISTORY ĐỂ READER RẢNH TAY
        // ==================================================================

        // Tìm History dựa trên Session ID (Giả sử trong History có trường request link với Session)
        History history = historyRepository.findByRequestId(sessionId)
                .orElseGet(() -> {
                    // Nếu không tìm thấy (cho các session cũ), ta chủ động tạo mới
                    return History.builder()
                            .customer(session.getCustomer())
                            .question(session.getQuestion())
                            .request(session)
                            .reader(session.getReader()) // Lấy reader từ session
                            .createdAt(java.time.LocalDateTime.now())
                            .build();
                });

        // Gắn Form vào History
        history.setInterpretationForm(savedForm);
        history.setStatus(ReadingStatus.WAITING_PAYMENT);

// 3. Lưu lại (Lệnh save này sẽ xử lý cả Update hoặc Insert mới)
        historyRepository.save(history);

        // ==================================================================

        return interpretationMapper.toDto(savedForm);
    }

    /**
     * Lấy dữ liệu bài luận cho Khách hàng xem (View)
     */
    public InterpretationResponseDto getForCustomer(Long sessionId) {
        InterpretationForm form = formRepository.findByRequestIdId(sessionId)
                .orElseThrow(() -> new RuntimeException("Bài luận giải chưa sẵn sàng."));
        
        return interpretationMapper.toDto(form);
    }

    /**
     * Reader xác nhận đã nhận được tiền từ Khách
     */
    @Transactional
    public void confirmPayment(Long sessionId) {
        InterpretationForm form = formRepository.findByRequestIdId(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy form luận giải."));

        // Update Form
        form.setStatus(InterpretationStatus.COMPLETED);

        // Update Session
        ReadingSession session = form.getRequestId();
        session.setStatus("COMPLETED");

        // Update History -> COMPLETED (Hoàn tất toàn bộ quy trình)
        History history = historyRepository.findByRequestId(sessionId)
                .orElseThrow(() -> new RuntimeException("Lỗi dữ liệu History"));
        history.setStatus(ReadingStatus.COMPLETED);
        history.setCompletedAt(java.time.LocalDateTime.now()); // Ghi nhận thời gian hoàn thành thật sự
        historyRepository.save(history);

        formRepository.save(form);
        sessionRepository.save(session);
    }
}