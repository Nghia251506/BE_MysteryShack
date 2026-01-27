package org.example.be_eproject_sem4.Service.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.be_eproject_sem4.Dto.ReadingSessionDTO;
import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Dto.SelectedCardDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.Topic;
import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Mapper.ReadingSessionMapper;
import org.example.be_eproject_sem4.Repository.QuestionRepository;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.TopicRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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
    private ReadingSessionMapper readingSessionMapper;

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
    public List<ReadingSessionSimpleDto> getMatchedSessionsForReader() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User reader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader đang đăng nhập"));

        if (!reader.getRole().equals(User.Role.READER)) {
            throw new RuntimeException("Chỉ reader mới xem được list matched");
        }

        // 1. Lấy Entity từ Repository
        List<ReadingSession> sessions = sessionRepository.findByReaderAndStatus(reader, "MATCHED");

        // 2. Dùng Mapper chuyển sang DTO trước khi return
        return readingSessionMapper.toSimpleDtoList(sessions);
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
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy session"));

        // Kiểm tra reader đang login có phải reader được ghép không
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        if (!session.getReader().equals(currentReader)) {
            throw new RuntimeException("Bạn không phải reader được ghép cho session này");
        }

        if (!"MATCHED".equals(session.getStatus())) {
            throw new RuntimeException("Session không ở trạng thái MATCHED");
        }

        session.setStatus("ACCEPTED");
        session.setAcceptedAt(Instant.now());
        sessionRepository.save(session);

        // Thông báo cho customer
        System.out.println("Thông báo cho customer: Request #" + sessionId + " đã được reader chấp nhận.");
    }

    @Transactional
    public void rejectSession(Long sessionId) {
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy session"));

        // Lấy Reader hiện tại
        User currentReader = userRepository
                .findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        // 1. Cập nhật trạng thái từ chối
        session.getRejectedReaderIds().add(currentReader.getId());
        session.setReader(null);
        session.setStatus("PENDING");
        session.setMatchedAt(null);
        sessionRepository.saveAndFlush(session); // Dùng saveAndFlush để đẩy data xuống DB ngay lập tức

        // 2. Chạy Async
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println(">>> Đang giữ request 5s trước khi tìm Reader mới...");
                Thread.sleep(5000);

                // QUAN TRỌNG: Gọi hàm thông qua một proxy hoặc truy vấn lại
                // Ở đây tôi gọi trực tiếp hàm xử lý với ID
                this.processReMatching(sessionId);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
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

        // 1. Lấy thông tin Authentication từ Context (đã được Filter nạp từ Cookie)
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra trạng thái đăng nhập và quyền hạn
        boolean isAuthenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            // Nhánh 1: Đã đăng nhập (Cookie hợp lệ)
            String username = auth.getName();
            customer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));

            // Log kiểm tra Role nếu cần (Tùy chọn)
            // boolean isCustomer = auth.getAuthorities().stream()
            // .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        } else {
            // Nhánh 2: Khách vãng lai (Không có cookie hoặc cookie hết hạn)
            if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng đăng nhập hoặc nhập họ tên");
            }
            if (dto.getBirthDate() == null) {
                throw new IllegalArgumentException("Vui lòng nhập ngày tháng năm sinh");
            }

            // Tạo user guest như cũ
            customer = new User();
            customer.setUsername("guest_" + UUID.randomUUID().toString().substring(0, 8));
            customer.setFullName(dto.getFullName());
            customer.setBirthDate(dto.getBirthDate());
            customer.setRole(User.Role.CUSTOMER);
            customer.setPasswordHash(passwordEncoder.encode("guest")); // Pass giả
            customer = userRepository.save(customer);
        }

        // 3. Tiếp tục tạo Session (Giữ nguyên logic của bạn)
        TopicQuestion question = questionRepository.findById(dto.getQuestion())
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại"));

        ReadingSession session = new ReadingSession();
        session.setCustomer(customer);
        session.setQuestion(question);
        session.setStatus("PENDING");

        session.setSelectedCards(dto.getSelectedCards());

        ReadingSession savedSession = sessionRepository.save(session);
        assignReaderToSession(savedSession, dto.getReaderId());
        session.setMatchedAt(Instant.now());

        return savedSession;
    }

    // Hàm ghép reader tự động (logic đơn giản: chọn reader có ELO cao nhất đang
    // verified)
    private void assignReaderToSession(ReadingSession session, Long chosenReaderId) {
        Set<Long> excludeIds = session.getRejectedReaderIds();
        User targetReader = null;

        // Trường hợp 1: Nếu FE có gửi lên Reader cụ thể
        if (chosenReaderId != null && !excludeIds.contains(chosenReaderId)) {
            targetReader = userRepository.findById(chosenReaderId).orElse(null);
        }

        // Trường hợp 2: Nếu không có chosenReaderId (hoặc người đó bị trùng trong list
        // từ chối)
        // thì mới dùng logic tìm người có Elo cao nhất
        if (targetReader == null) {
            List<User> readers = userRepository.findAllByRoleAndIsVerifiedOrderByEloScoreDesc(User.Role.READER, true);
            targetReader = readers.stream()
                    .filter(r -> !excludeIds.contains(r.getId()))
                    .findFirst()
                    .orElse(null);
        }

        if (targetReader != null) {
            session.setReader(targetReader);
            session.setStatus("MATCHED");
            sessionRepository.save(session);
            System.out.println(">>> [MATCH SUCCESS] Assigned Reader: " + targetReader.getFullName());
        } else {
            session.setStatus("PENDING");
            sessionRepository.save(session);
            System.out.println(">>> [MATCH FAILED] Không có Reader khả dụng.");
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
}
