package org.example.be_eproject_sem4.Service.Interpretation;

import java.util.List;
import java.util.Map;

import org.example.be_eproject_sem4.Dto.InterpretationResponseDto;
import org.example.be_eproject_sem4.Dto.InterpretationSubmitDto;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Mapper.InterpretationMapper;
import org.example.be_eproject_sem4.Repository.FcmTokenRepository;
import org.example.be_eproject_sem4.Repository.HistoryRepository;
import org.example.be_eproject_sem4.Repository.InterpretationFormRepository;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Service.FCM.FCMService;
import org.example.be_eproject_sem4.Service.FCM.FcmTokenService;
import org.example.be_eproject_sem4.Service.FCM.NotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final FCMService fcmService;
    private final FcmTokenService fcmTokenService;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationManager notificationManager;

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

        // 2. Tạo Form luận giải mới
        InterpretationForm form = new InterpretationForm();
        form.setRequestId(session);
        form.setInterpretation1(dto.getInterpretation1());
        form.setInterpretation2(dto.getInterpretation2());
        form.setInterpretation3(dto.getInterpretation3());
        form.setAdvice(dto.getAdvice());
        if (session.getReader() != null) {
            // Ưu tiên lấy QR trực tiếp từ hồ sơ Reader trong Database
            form.setQrPayment(session.getReader().getQRCode());
        } else if (dto.getQrPayment() != null) {
            // Nếu hồ sơ không có thì mới lấy từ DTO nộp lên
            form.setQrPayment(dto.getQrPayment().getQRCode());
        }

        // Kiểm tra lại lần cuối trước khi lưu
        if (form.getQrPayment() == null) {
            throw new RuntimeException("Reader chưa cập nhật mã QR thanh toán!");
        }
        if (dto.getInterpretation1() == null || dto.getInterpretation1().trim().isEmpty()) {
            throw new RuntimeException("Nội dung luận giải lá bài 1 không được để trống.");
        }

        form.setStatus(InterpretationStatus.SENT_TO_CUSTOMER);
        session.setStatus("INTERPRETED");
        session.setAmount(dto.getAmount());

        InterpretationForm savedForm = formRepository.save(form);

        // 3. Cập nhật History
        History history = historyRepository.findByRequestId(sessionId)
                .orElseGet(() -> History.builder()
                        .customer(session.getCustomer())
                        .question(session.getQuestion())
                        .request(session)
                        .reader(session.getReader())
                        .createdAt(java.time.LocalDateTime.now())
                        .build());

        history.setInterpretationForm(savedForm);
        history.setStatus(ReadingStatus.WAITING_PAYMENT);
        history.setInterpretationForm(form);
        historyRepository.save(history);

        // ==================================================================
        // 4. LOGIC FCM: THÔNG BÁO CHO CẢ READER VÀ CUSTOMER
        // ==================================================================

        // A. Thông báo cho Reader (Xác nhận thành công)
        notificationManager.notifyReadingFinished(
            session.getCustomer().getId(), 
            sessionId, 
            session.getReader().getFullName()
        );

        return interpretationMapper.toDto(savedForm);
    }

    /**
     * Tái sử dụng hàm bổ trợ đã viết ở các luồng trước
     */
    private void sendNotificationToUser(User user, String title, String body, String sessionId, String type) {
        if (user == null)
            return;
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(user.getId());
        if (tokens != null && !tokens.isEmpty()) {
            Map<String, String> data = Map.of(
                    "sessionId", sessionId,
                    "type", type);
            tokens.forEach(t -> fcmService.sendPushNotification(t.getToken(), title, body, data));
        }
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
        Long customerId = session.getCustomer().getId();
        notificationManager.notifyCustomerPaymentConfirmed(
            session.getCustomer().getId(), 
            sessionId
        );
    }

    /**
     * Khách hàng ấn nút "Tôi đã thanh toán"
     * Cập nhật trạng thái để Reader biết và bắn thông báo Push
     */
    @Transactional
    public void customerNotifyPaid(Long sessionId) {
        // 1. Tìm Form luận giải
        InterpretationForm form = formRepository.findByRequestIdId(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy form luận giải."));

        // 2. Cập nhật trạng thái Form sang PAID (Chờ xác nhận)
        // Lưu ý: Bạn có thể thêm InterpretationStatus.PAID vào Enum của mình
        form.setStatus(InterpretationStatus.PAID);
        formRepository.save(form);

        // 3. Cập nhật trạng thái History để Reader thấy màu sắc thay đổi trong danh
        // sách
        History history = historyRepository.findByRequestId(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử phiên dịch."));
        // history.setStatus(ReadingStatus.PAID); // Nếu bạn có status này ở History
        historyRepository.save(history);

        // 4. BẮN THÔNG BÁO CHO READER
        // Lấy thông tin Reader từ Session
        if (form.getRequestId().getReader() != null) {
            notificationManager.notifyReaderPaymentSent(
                    form.getRequestId().getReader().getId(),
                sessionId, 
                form.getRequestId().getFullName() // Gửi thêm tên để Reader biết ai trả
            );
        }
    }
}