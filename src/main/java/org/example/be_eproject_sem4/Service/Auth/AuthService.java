package org.example.be_eproject_sem4.Service.Auth;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Dto.Auth.AuthResponseDto;
import org.example.be_eproject_sem4.Dto.Auth.LoginRequest;
import org.example.be_eproject_sem4.Dto.Auth.RegisterRequestDto;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
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

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ReadingSessionRepository sessionRepository;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            ReadingSessionRepository sessionRepository,
            @org.springframework.context.annotation.Lazy AuthenticationManager authenticationManager) { // Thêm @Lazy ở
                                                                                                        // đây
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionRepository = sessionRepository;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User.Role userRole = User.Role.CUSTOMER; // Mặc định
        if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("READER")) {
            userRole = User.Role.READER;
            // Nếu muốn giới hạn: chỉ admin mới set được role READER
            // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // if (auth == null || !auth.getAuthorities().contains(new
            // SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // throw new RuntimeException("Chỉ admin mới đăng ký được reader");
            // }
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .role(userRole)
                .isVerified(false) // Reader cần verify sau
                .eloScore(1000)
                .build();

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .user(UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .isVerified(user.isVerified())
                        .eloScore(user.getEloScore())
                        .build())
                .build();
    }

    public AuthResponseDto login(LoginRequest dto, HttpServletResponse response) {
        // 1. Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // 2. Tạo JWT Token
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

        // 4. XỬ LÝ LOGIC READER (Đã sửa lỗi ép kiểu tại đây)
        List<ReadingSessionSimpleDto> matchedSessionsList = null; // Khai báo đúng kiểu List
        if (user.getRole().equals(User.Role.READER)) {
            // Repository bây giờ trả về List nên sẽ khớp hoàn toàn
            matchedSessionsList = sessionRepository.findByReaderAndStatus(user, "MATCHED");
        }

        return AuthResponseDto.builder()
                .token(token)
                .user(UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .isVerified(user.isVerified())
                        .eloScore(user.getEloScore())
                        // TRUYỀN ĐÚNG BIẾN matchedSessionsList VÀO ĐÂY
                        .matchedSessions(matchedSessionsList)
                        .build())
                .build();
    }
}