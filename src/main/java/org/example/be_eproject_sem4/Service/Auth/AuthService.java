package org.example.be_eproject_sem4.Service.Auth;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Dto.Auth.AuthResponseDto;
import org.example.be_eproject_sem4.Dto.Auth.LoginRequest;
import org.example.be_eproject_sem4.Dto.Auth.RegisterRequestDto;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
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

        public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider,
                        ReadingSessionRepository sessionRepository,
                        ReadingSessionMapper readingSessionMapper,
                        @org.springframework.context.annotation.Lazy AuthenticationManager authenticationManager) { 
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtTokenProvider = jwtTokenProvider;
                this.sessionRepository = sessionRepository;
                this.authenticationManager = authenticationManager;
                this.readingSessionMapper = readingSessionMapper;
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
        @Transactional
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
                List<ReadingSessionSimpleDto> matchedSessionsList = new ArrayList<>();

                if (user.getRole().equals(User.Role.READER)) {
                        // Lấy danh sách Entity (Đảm bảo Repository trả về List<ReadingSession>)
                        List<ReadingSession> sessions = sessionRepository.findByReaderAndStatus(user, "MATCHED");

                        // Sử dụng MapStruct để chuyển đổi sạch sẽ
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
                                                .role(user.getRole().name())
                                                .isVerified(user.isVerified())
                                                .eloScore(user.getEloScore())
                                                // TRUYỀN ĐÚNG BIẾN matchedSessionsList VÀO ĐÂY
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
}