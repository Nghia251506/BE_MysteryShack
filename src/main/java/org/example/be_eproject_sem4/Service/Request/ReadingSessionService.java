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

    // 3. Tạo mới một phiên đọc
    @Transactional
    public ReadingSession createSession(ReadingSessionDTO dto) {
        User customer;

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Nhánh 1: Đã đăng nhập
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            String username = auth.getName();
            customer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + username));
        }
        // Nhánh 2: Chưa đăng nhập (khách vãng lai) → tạo user guest
        else {
            if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập họ tên khi chưa đăng nhập");
            }
            if (dto.getBirthDate() == null) {
                throw new IllegalArgumentException("Vui lòng nhập ngày tháng năm sinh khi chưa đăng nhập");
            }

            // Tạo user guest
            customer = new User();
            customer.setUsername("guest_" + UUID.randomUUID().toString().substring(0, 8)); // Username tạm unique
            customer.setFullName(dto.getFullName());
            // Nếu entity User có field birthDate → set luôn
            customer.setBirthDate(dto.getBirthDate());
            // Nếu không, lưu tạm vào bio hoặc customerQuestion
            customer.setBio("Guest - Sinh: " + dto.getBirthDate()); // Lưu tạm vào bio
            customer.setEmail("guest_" + System.currentTimeMillis() + "@temp.com"); // Email tạm
            customer.setPasswordHash("guest"); // Không cần pass thật
            customer.setRole(User.Role.CUSTOMER);
            customer.setVerified(false);
            customer.setEloScore(1000);

            // LƯU USER GUEST VÀO DB → có ID thật
            customer = userRepository.save(customer);
        }

        // Lấy question
        TopicQuestion question = questionRepository.findById(dto.getQuestion())
                .orElseThrow(() -> new RuntimeException("Câu hỏi ID " + dto.getQuestion() + " không tồn tại"));

        // Tạo session
        ReadingSession session = new ReadingSession();
        session.setCustomer(customer); // ← Đã có ID thật (guest hoặc user thật)
        session.setQuestion(question);
        session.setReader(null);
        session.setStatus("PENDING");

        // selectedCards → JSON
        if (dto.getSelectedCards() == null || dto.getSelectedCards().isEmpty()) {
            throw new IllegalArgumentException("Danh sách lá bài không được để trống");
        }
        String jsonCards = objectMapper.writeValueAsString(dto.getSelectedCards());
        session.setSelectedCards(jsonCards);

        // Lưu customerQuestion nếu có
        // session.setCustomerQuestion(dto.getCustomerQuestion());

        // Save session
        ReadingSession savedSession = sessionRepository.save(session);

        // Ghép reader tự động (nếu muốn)
        assignReaderToSession(savedSession);

        // Thông báo
        if (customer.getUsername().startsWith("guest_")) {
            System.out.println("Thông báo cho khách vãng lai (" + dto.getFullName() + "): Request #"
                    + savedSession.getId() + " tạo thành công.");
        } else {
            System.out.println("Thông báo cho khách hàng " + customer.getUsername() + ": Request #"
                    + savedSession.getId() + " tạo thành công.");
        }

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
