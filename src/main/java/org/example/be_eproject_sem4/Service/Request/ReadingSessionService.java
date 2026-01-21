package org.example.be_eproject_sem4.Service.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.be_eproject_sem4.Dto.ReadingSessionDTO;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.Topic;
import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.QuestionRepository;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.TopicRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
public class ReadingSessionService {

    @Autowired
    private ReadingSessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

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

    public List getMatchedSessionsForReader() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User reader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader đang đăng nhập"));

        if (!reader.getRole().equals(User.Role.READER)) {
            throw new RuntimeException("Chỉ reader mới xem được list matched");
        }

        return sessionRepository.findByReaderAndStatus(reader, "MATCHED");
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
        sessionRepository.save(session);

        // Thông báo cho customer
        System.out.println("Thông báo cho customer: Request #" + sessionId + " đã được reader chấp nhận.");
    }

    @Transactional
    public void rejectSession(Long sessionId) {
        ReadingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy session"));

        // Kiểm tra reader đang login
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        if (!session.getReader().equals(currentReader)) {
            throw new RuntimeException("Bạn không phải reader được ghép cho session này");
        }

        if (!"MATCHED".equals(session.getStatus())) {
            throw new RuntimeException("Session không ở trạng thái MATCHED");
        }

        // Reject: Xóa reader, chuyển về PENDING để hệ thống tìm lại
        session.setReader(null);
        session.setStatus("PENDING");
        sessionRepository.save(session);

        // Tự động tìm reader mới
        assignReaderToSession(session);

        // Thông báo cho customer
        System.out.println(
                "Thông báo cho customer: Reader từ chối request #" + sessionId + ", hệ thống đang tìm reader mới.");
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
            customer.setPasswordHash("guest"); // Pass giả
            customer = userRepository.save(customer);
        }

        // 3. Tiếp tục tạo Session (Giữ nguyên logic của bạn)
        TopicQuestion question = questionRepository.findById(dto.getQuestion())
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại"));

        ReadingSession session = new ReadingSession();
        session.setCustomer(customer);
        session.setQuestion(question);
        session.setStatus("PENDING");

        String jsonCards = objectMapper.writeValueAsString(dto.getSelectedCards());
        session.setSelectedCards(jsonCards);

        ReadingSession savedSession = sessionRepository.save(session);
        assignReaderToSession(savedSession);

        return savedSession;
    }

    // Hàm ghép reader tự động (logic đơn giản: chọn reader có ELO cao nhất đang
    // verified)
    private void assignReaderToSession(ReadingSession session) {
        // Tìm reader có ELO cao nhất, verified, role READER
        User bestReader = userRepository.findFirstByRoleAndIsVerifiedOrderByEloScoreDesc(User.Role.READER, true);

        if (bestReader != null) {
            session.setReader(bestReader);
            session.setStatus("MATCHED");
            sessionRepository.save(session);

            // Thông báo cho reader
            System.out.println("Thông báo cho reader " + bestReader.getUsername() + ": Bạn được ghép với request #"
                    + session.getId());

            // Thông báo cho customer
            System.out.println("Thông báo cho customer " + session.getCustomer().getUsername()
                    + ": Request của bạn đã được ghép với reader " + bestReader.getUsername());
        } else {
            // Không có reader → để PENDING, sau dùng scheduler tìm lại
            System.out.println("Không tìm thấy reader nào, session #" + session.getId() + " đang chờ.");
        }
    }

    // 4. Cập nhật phiên đọc (Thường dùng để cập nhật kết quả sau khi trải bài)
    @Transactional
    public ReadingSession updateSession(Long id, ReadingSessionDTO dto) {
        ReadingSession existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên đọc để cập nhật"));

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
