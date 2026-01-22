package org.example.be_eproject_sem4.Service.Interpretation;

import org.example.be_eproject_sem4.Dto.InterpretationResponseDto;
import org.example.be_eproject_sem4.Dto.InterpretationSubmitDto;
import org.example.be_eproject_sem4.Entity.InterpretationForm;
import org.example.be_eproject_sem4.Entity.InterpretationStatus;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Mapper.InterpretationMapper;
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

    /**
     * Reader nộp bài luận giải cho 3 lá bài kèm lời khuyên và QR
     */
    @Transactional
    public InterpretationResponseDto submitInterpretation(Long sessionId, InterpretationSubmitDto dto) {
        // 1. Tìm và kiểm tra Session
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng (Session)"));

        // Kiểm tra xem đơn đã được Reader chấp nhận chưa
        if (!"ACCEPTED".equals(session.getStatus())) {
            throw new RuntimeException("Đơn hàng phải ở trạng thái ACCEPTED mới có thể nộp bài.");
        }

        // 2. Tạo Form luận giải mới
        InterpretationForm form = new InterpretationForm();
        form.setRequestId(session);
        
        // Gán nội dung cho từng lá bài từ DTO
        form.setInterpretation1(dto.getInterpretation1());
        form.setInterpretation2(dto.getInterpretation2());
        form.setInterpretation3(dto.getInterpretation3());
        
        form.setAdvice(dto.getAdvice());
        form.setQrPayment(dto.getQrPayment());

        // Kiểm tra an toàn: Ít nhất lá bài 1 phải có nội dung để khách xem preview
        if (dto.getInterpretation1() == null || dto.getInterpretation1().trim().isEmpty()) {
            throw new RuntimeException("Nội dung luận giải lá bài 1 không được để trống.");
        }

        // 3. Cập nhật trạng thái
        // Chuyển Form sang SENT_TO_CUSTOMER để khách có thể quét QR và xem lá 1
        form.setStatus(InterpretationStatus.SENT_TO_CUSTOMER);
        
        // Chuyển Session sang INTERPRETED (Đã luận giải xong)
        session.setStatus("INTERPRETED");

        InterpretationForm savedForm = formRepository.save(form);

        // 4. Trả về DTO (Mapper sẽ tự động ẩn lá 2, 3 nếu chưa thanh toán)
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy form luận giải cho phiên này."));

        // Chuyển trạng thái Form sang PAID và COMPLETED
        form.setStatus(InterpretationStatus.PAID);
        form.setStatus(InterpretationStatus.COMPLETED);

        // Kết thúc Reading Session
        ReadingSession session = form.getRequestId();
        session.setStatus("COMPLETED");

        formRepository.save(form);
        sessionRepository.save(session);
    }
}