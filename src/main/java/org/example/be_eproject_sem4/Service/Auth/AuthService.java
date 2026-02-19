package org.example.be_eproject_sem4.Service.Auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.example.be_eproject_sem4.Dto.Auth.AuthResponseDto;
import org.example.be_eproject_sem4.Dto.Auth.ChangePasswordDto;
import org.example.be_eproject_sem4.Dto.Auth.LoginRequest;
import org.example.be_eproject_sem4.Dto.Auth.RegisterRequestDto;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Mapper.ReadingSessionMapper;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ReadingSessionRepository sessionRepository;
    private final ReadingSessionMapper readingSessionMapper;
    private final org.example.be_eproject_sem4.Service.Mail.EmailService emailService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            ReadingSessionRepository sessionRepository,
            ReadingSessionMapper readingSessionMapper,
            @org.springframework.context.annotation.Lazy AuthenticationManager authenticationManager,
            org.example.be_eproject_sem4.Service.Mail.EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionRepository = sessionRepository;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.readingSessionMapper = readingSessionMapper;
    }

    public AuthResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // 1. Tạo verification token
        String verificationToken = UUID.randomUUID().toString();

        User.Role userRole = User.Role.CUSTOMER;
        if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("READER")) {
            userRole = User.Role.READER;
        }

        // 2. Build User với thông tin verify
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .birthDate(dto.getBirthDate())
                .phone(dto.getPhone())
                .role(userRole)
                .isVerified(false) // Mặc định là false
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24)) // Hết hạn sau 24h
                .eloScore(500)
                .build();

        user = userRepository.save(user);

        // 3. Gửi Mail xác thực (Nên dùng @Async trong EmailService để không làm chậm
        // response)
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getId());
        } catch (Exception e) {
            // Log lỗi gửi mail nhưng vẫn cho User đăng ký, hoặc xử lý tùy ông
            System.err.println("Lỗi gửi mail: " + e.getMessage());
        }

        // 4. Trả về thông báo (Ở đây tôi vẫn trả JWT, nhưng ông nên check isVerified ở
        // filter/login)
        String token = jwtTokenProvider.generateToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .user(UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .isVerified(user.isVerified())
                        .build())
                .build();
    }

    @Transactional
    public AuthResponseDto login(LoginRequest dto, HttpServletResponse response) {
        // 1. Xác thực người dùng (Kiểm tra username/password trước)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // --- ĐOẠN CHECK QUAN TRỌNG NHẤT Ở ĐÂY ---
        if (!user.isVerified()) {
            throw new RuntimeException("Tài khoản chưa được xác thực. Vui lòng kiểm tra email của bạn!");
        }
        // ---------------------------------------

        // 2. Tạo JWT Token (Chỉ khi đã verify mới đi tiếp đến đây)
        String token = jwtTokenProvider.generateToken(user);

        // 3. TẠO VÀ CẤU HÌNH COOKIE
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "");
        response.addCookie(cookie);

        // 4. XỬ LÝ LOGIC READER
        List<ReadingSessionSimpleDto> matchedSessionsList = new ArrayList<>();
        if (user.getRole().equals(User.Role.READER)) {
            List<ReadingSession> sessions = sessionRepository.findByReaderAndStatus(user, "MATCHED");
            matchedSessionsList = readingSessionMapper.toSimpleDtoList(sessions);
        }

        return AuthResponseDto.builder()
                .token(token)
                .user(UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .birthDate(user.getBirthDate())
                        .profilePicture(user.getProfilePicture())
                        .qrCode(user.getQRCode())
                        .role(user.getRole().name())
                        .isVerified(user.isVerified())
                        .eloScore(user.getEloScore())
                        .matchedSessions(matchedSessionsList)
                        .build())
                .build();
    }

    public void logout(HttpServletResponse response) {
        // 1. Xóa SecurityContext trên Server
        SecurityContextHolder.clearContext();

        // 2. Tạo cookie ghi đè để xóa
        Cookie cookie = new Cookie("access_token", null);

        // Các thuộc tính phải khớp với lúc Login để trình duyệt nhận diện đúng
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        // Set thời gian sống bằng 0 để xóa ngay lập tức
        cookie.setMaxAge(0);

        // Thêm các thuộc tính nâng cao nếu bạn đã dùng lúc Login
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "");

        // Gửi cookie về client để thực hiện xóa
        response.addCookie(cookie);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = ((Optional<User>) userRepository.findByEmail(email))
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        if (user.isVerified()) {
            throw new RuntimeException("Tài khoản này đã được xác thực rồi!");
        }

        // Tạo token mới, reset lại 24h mới
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Gửi lại mail
        emailService.sendVerificationEmail(user.getEmail(), newToken, user.getId());
    }

    @Transactional
    public void resetPassword(ChangePasswordDto request) {
        // Tìm theo email lấy từ request (FE gửi lên từ param trên URL)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dấu vết thần bí của email này!"));

        // Vì đi từ Mail nên currentPassword sẽ null -> Ta bỏ qua bước check pass cũ
        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
            }
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận không đồng nhất!");
        }

        // "Thay áo mới" cho mật khẩu
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    @Transactional
    public void requestForgotPassword(String email) {
        // 1. Check xem mail có trong DB không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa được đăng ký trong cõi Mystic!"));

        // 2. Nếu có, bắn mail ngay và luôn
        // Ở đây ông truyền email của khách vào link để FE lấy được
        emailService.sendForgotPasswordEmail(email);
    }
}
