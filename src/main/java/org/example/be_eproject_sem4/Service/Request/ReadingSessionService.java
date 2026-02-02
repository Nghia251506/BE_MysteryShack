package org.example.be_eproject_sem4.Service.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.be_eproject_sem4.Dto.ReadingSessionDTO;
import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Dto.SelectedCardDto;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Mapper.ReadingSessionMapper;
import org.example.be_eproject_sem4.Repository.*;
import org.example.be_eproject_sem4.Service.FCM.FCMService;
import org.example.be_eproject_sem4.Service.FCM.FcmTokenService;
import org.example.be_eproject_sem4.Service.FCM.NotificationManager;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ReadingSessionService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ReadingSessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ReadingSessionMapper readingSessionMapper;
    @Autowired
    private FCMService fcmService;

    @Autowired
    private FcmTokenService fcmTokenService;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private NotificationManager notificationManager;

    ReadingSessionService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Lấy tất cả các phiên đọc
    public List<ReadingSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    // 2. Lấy chi tiết phiên đọc theo ID
    public ReadingSession getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên đọc ID: " + id));
    }

    @Transactional
    public BigDecimal getTotalIncomeForReader() {
        // A. Lấy thông tin Reader đang đăng nhập
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User reader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        // B. Tìm các session có trạng thái COMPLETED (hoặc status = 2 tùy DB của ông)
        List<ReadingSession> completedSessions = sessionRepository.findByReaderAndStatus(reader, "COMPLETED");

        // C. Tính tổng amount bằng Stream
        // Sử dụng reduce để cộng BigDecimal an toàn
        return completedSessions.stream()
                .map(ReadingSession::getAmount)
                .filter(amount -> amount != null) // Lọc bỏ trường hợp amount bị null cho chắc
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional()
    public Long getTotalCompletedSessionsForReader() {
        // A. Lấy thông tin Reader đang đăng nhập từ Security Context
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User reader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader đang đăng nhập"));

        // B. Gọi Repo với tham số status là "COMPLETED"
        // Ông có thể dùng String cứng hoặc một Enum/Constant nếu có
        Long totalSessions = sessionRepository.countCompletedSessionsByReader(reader, "COMPLETED");

        return totalSessions != null ? totalSessions : 0L;
    }

    @Transactional
    public List<ReadingSessionSimpleDto> getReadingSessionsForCustomer() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader đang đăng nhập"));

        if (!customer.getRole().equals(User.Role.READER)) {
            throw new RuntimeException("Chỉ reader mới xem được list matched");
        }

        // 1. Lấy Entity từ Repository
        List<ReadingSession> sessions = sessionRepository.findByReaderAndStatus(customer, "MATCHED");

        // 2. Dùng Mapper chuyển sang DTO trước khi return
        return readingSessionMapper.toSimpleDtoList(sessions);
    }

    @Transactional
    public void acceptSession(Long sessionId) {
        // 1. Tìm session và Reader hiện tại
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy session"));

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        // 2. Kiểm tra quyền và trạng thái (Giữ nguyên logic của bạn)
        if (!session.getReader().equals(currentReader)) {
            throw new RuntimeException("Bạn không phải reader được ghép cho session này");
        }
        if (!"MATCHED".equals(session.getStatus())) {
            throw new RuntimeException("Session không ở trạng thái MATCHED");
        }

        // 3. Cập nhật Session và Lịch sử
        session.setStatus("PROCESSING");
        session.setAcceptedAt(Instant.now());
        session.getReader().setEloBeforeAction(session.getReader().getEloScore());
        session.getReader().setIsBusy(true);
        sessionRepository.save(session);
        updateHistoryStatus(sessionId, ReadingStatus.PROCESSING, currentReader);

        // 4. LOGIC GỬI FCM THỰC THẾ
        notificationManager.notifyCustomerAccepted(session.getCustomer().getId(), currentReader.getFullName());

        System.out.println("Thông báo cho customer: Request #" + sessionId + " đã được reader chấp nhận.");
    }

    @Transactional
    public void rejectSession(Long sessionId) {
        // 1. Tìm Session và Reader hiện tại
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy session"));

        User currentReader = userRepository
                .findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        // 2. Lưu thông tin khách hàng để gửi thông báo trước khi reset session
        User customer = session.getCustomer();

        // 3. Cập nhật trạng thái Session (Reject Reader này và quay về PENDING)
        session.getRejectedReaderIds().add(currentReader.getId());
        session.setReader(null);
        session.setStatus("PENDING");
        session.setMatchedAt(null);
        sessionRepository.saveAndFlush(session);

        // 4. Cập nhật lịch sử trạng thái
        updateHistoryStatus(sessionId, ReadingStatus.PENDING, null);

        // --- LUỒNG GỬI FCM CHO KHÁCH HÀNG ---
        notificationManager.notifyCustomerReaderRejected(customer.getId(), currentReader.getFullName());

        // 5. Chạy Async để tìm Reader mới sau 5 giây
        CompletableFuture.runAsync(() -> {
            try {
                // System.out.println(">>> Reader từ chối. Đang đợi 5s để tìm người mới cho
                // Session: " + sessionId);
                Thread.sleep(5000);
                this.processReMatching(sessionId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Transactional()
    public ReadingSession getLatestProcessingSession(Long readerId) {
        // PageRequest.of(trang_so, kich_thuoc) -> lấy trang 0, chỉ 1 bản ghi
        List<ReadingSession> sessions = sessionRepository.findCurrentProcessingSession(
                readerId, PageRequest.of(0, 1)
        );

        return sessions.isEmpty() ? null : sessions.get(0);
    }

    // Hàm này xử lý việc tìm kiếm lại
    public void processReMatching(Long sessionId) {
        ReadingSession session = sessionRepository.findById(sessionId).orElse(null);

        if (session != null && "PENDING".equals(session.getStatus())) {
            System.out.println(">>> Tìm Reader mới thay thế cho Session: " + sessionId);
            // Khi Re-match thì chosenReaderId truyền vào là null để hệ thống tự tìm người
            // mới
            assignReaderToSession(session, null);
            session.setMatchedAt(Instant.now());
            sessionRepository.save(session);
        }
    }

    // 3. Tạo mới một phiên đọc
    @Transactional
    public ReadingSession createSession(ReadingSessionDTO dto) {
        User customer;

        // --- Logic xác thực user (giữ nguyên) ---
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            String username = auth.getName();
            customer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
        } else {
            // Logic tạo guest (giữ nguyên)
            if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng đăng nhập hoặc nhập họ tên");
            }
            customer = new User();
            customer.setUsername("guest_" + UUID.randomUUID().toString().substring(0, 8));
            customer.setFullName(dto.getFullName());
            customer.setBirthDate(dto.getBirthDate());
            customer.setRole(User.Role.CUSTOMER);
            customer.setPasswordHash(passwordEncoder.encode("guest"));
            customer = userRepository.save(customer);
        }

        TopicQuestion question = questionRepository.findById(dto.getQuestion())
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại"));

        // 1. Lưu Session trước
        ReadingSession session = new ReadingSession();
        session.setCustomer(customer);
        session.setQuestion(question);
        session.setStatus("PENDING");
        session.setSelectedCards(dto.getSelectedCards());
        ReadingSession savedSession = sessionRepository.save(session);

        // 2. TẠO HISTORY NGAY LẬP TỨC (QUAN TRỌNG)
        // Để khách hàng thấy ngay yêu cầu trong lịch sử là "Đang tìm Reader"
        History history = History.builder()
                .customer(customer)
                .question(question)
                .request(savedSession)
                .status(ReadingStatus.PENDING) // Trạng thái ban đầu
                .createdAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);
        notificationManager.notifyCustomerSearching(customer.getId());
        // 3. Tiến hành ghép Reader (Sẽ update lại History nếu tìm thấy)
        assignReaderToSession(savedSession, dto.getReaderId());
        savedSession.setMatchedAt(Instant.now());

        return savedSession;
    }

    // Hàm ghép reader tự động (logic đơn giản: chọn reader có ELO cao nhất đang
    // verified)
    private void assignReaderToSession(ReadingSession session, Long chosenReaderId) {
        Set<Long> excludeIds = session.getRejectedReaderIds();
        User targetReader = null;

        // 1. Logic tìm Reader (giữ nguyên)
        if (chosenReaderId != null && !excludeIds.contains(chosenReaderId)) {
            targetReader = userRepository.findById(chosenReaderId).orElse(null);
        }

        if (targetReader == null) {
            List<User> readers = userRepository.findAllByRoleAndIsVerifiedOrderByEloScoreDesc(User.Role.READER, true);
            targetReader = readers.stream()
                    .filter(r -> !excludeIds.contains(r.getId()))
                    .findFirst()
                    .orElse(null);
        }

        if (targetReader != null) {
            // 2. Cập nhật DB
            session.setReader(targetReader);
            session.setStatus("MATCHED");
            session.setMatchedAt(Instant.now());
            sessionRepository.save(session);
            System.out.println(">>> [MATCH SUCCESS] Assigned Reader: " + targetReader.getFullName());

            // --- GỬI THÔNG BÁO CHO READER ---
            notificationManager.notifyReaderNewRequest(session.getReader().getId(), session.getId(),session.getFullName());
            notificationManager.notifyReaderMatched(session.getCustomer().getId(),session.getReader().getFullName());
        } else {
            session.setStatus("PENDING");
            sessionRepository.save(session);
            notificationManager.notifyCustomerSearching(session.getCustomer().getId());
            System.out.println(">>> [MATCH FAILED] Không có Reader khả dụng.");
        }
    }

    /**
     * Hàm bổ trợ để tái sử dụng logic gửi thông báo cho User bất kỳ (Reader hoặc
     * Customer)
     */
    private void sendNotificationToUser(User user, String title, String body, String sessionId, String type) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(user.getId());
        if (tokens != null && !tokens.isEmpty()) {
            Map<String, String> data = Map.of(
                    "sessionId", sessionId,
                    "type", type);
            tokens.forEach(t -> fcmService.sendPushNotification(t.getToken(), title, body, data));
        }
    }

    // 4. Cập nhật phiên đọc (Thường dùng để cập nhật kết quả sau khi trải bài)
    @Transactional
    public ReadingSession updateSession(Long id, ReadingSessionDTO dto) {
        ReadingSession existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên đọc để cập nhật"));

        if (dto.getSelectedCards() != null) {
            existingSession.setSelectedCards(dto.getSelectedCards());
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

    private void updateHistoryStatus(Long sessionId, ReadingStatus newStatus, User reader) {
        History history = historyRepository.findByRequestId(sessionId).orElse(null);
        if (history != null) {
            history.setStatus(newStatus);
            // Nếu newStatus là PENDING (do reject) -> set reader = null
            // Nếu có truyền reader vào (do accept) -> set reader
            if (newStatus == ReadingStatus.PENDING) {
                history.setReader(null);
            } else if (reader != null) {
                history.setReader(reader);
            }
            historyRepository.save(history);
        }
    }
}
