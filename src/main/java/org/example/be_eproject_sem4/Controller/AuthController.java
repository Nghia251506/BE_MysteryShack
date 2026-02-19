package org.example.be_eproject_sem4.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.example.be_eproject_sem4.Dto.Auth.AuthResponseDto;
import org.example.be_eproject_sem4.Dto.Auth.ChangePasswordDto;
import org.example.be_eproject_sem4.Dto.Auth.LoginRequest;
import org.example.be_eproject_sem4.Dto.Auth.RegisterRequestDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.Auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Import cái này nhé ông giáo
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final org.example.be_eproject_sem4.Repository.UserRepository userRepository;
    private final org.example.be_eproject_sem4.Service.Mail.EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // Gọi service và truyền response vào để service set cookie
        AuthResponseDto authResponse = authService.login(loginRequest, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    @GetMapping("/public/verify")
    public void verifyUser(
            @RequestParam String token,
            @RequestParam Long userId,
            HttpServletResponse response) throws IOException { // Thêm response vào đây

        // 1. Tìm User
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.sendRedirect("https://mystictarots.xyz/verify-result?status=invalid");
            return;
        }

        // 2. Nếu đã verify rồi
        if (user.isVerified()) {
            response.sendRedirect("https://mystictarots.xyz/verify-result?status=success");
            return;
        }

        // 3. Kiểm tra Token
        if (user.getVerificationToken() == null || !user.getVerificationToken().equals(token)) {
            response.sendRedirect("https://mystictarots.xyz/verify-result?status=invalid");
            return;
        }

        // 4. Kiểm tra hết hạn (Expiry)
        if (user.getVerificationTokenExpiry() != null &&
                user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            response.sendRedirect("https://mystictarots.xyz/verify-result?status=expired&email=" + user.getEmail());
            return;
        }

        // 5. Kích hoạt tài khoản
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        if (user.getRole() != null && user.getRole().equals("READER")) {
            user.setActive(false);
        } else {
            user.setActive(true);
        }

        userRepository.save(user);

        // Thành công rực rỡ thì bay về đây
        response.sendRedirect("https://mystictarots.xyz/verify-result?status=success");
    }

    @PostMapping("/public/resend-verify")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Email không tồn tại.");
        }

        // Tạo token mới & expiry mới (24h)
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Gửi mail mới
        emailService.sendVerificationEmail(user.getEmail(), newToken, user.getId());

        return ResponseEntity.ok("Mã xác thực mới đã được gửi vào email của bạn.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        authService.requestForgotPassword(email);
        return ResponseEntity.ok("Một mật chỉ đã được gửi đến email của bạn. Hãy kiểm tra hộp thư!");
    }

    // 2. Endpoint thực hiện đổi mật khẩu (Khách từ mail về, điền form rồi ấn submit)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordDto request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Mật chú đã được thay đổi thành công. Bạn có thể đăng nhập bằng năng lượng mới!");
    }
}