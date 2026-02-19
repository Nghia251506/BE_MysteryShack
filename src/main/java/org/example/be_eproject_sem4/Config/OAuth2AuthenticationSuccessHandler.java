package org.example.be_eproject_sem4.Config;

import java.io.IOException;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtUtils;
    private final org.example.be_eproject_sem4.Repository.UserRepository userRepository; // Repo để tìm User sau khi
    // login bằng Google

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 1. Lấy đúng đối tượng OAuth2User
        org.springframework.security.oauth2.core.user.OAuth2User oAuth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication
                .getPrincipal();

        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);
        if (email == null) {
            email = oAuth2User.getAttribute("id") + "@facebook.com";
        }

        if (user == null) {
            response.sendRedirect("https://mystictarots.xyz/login?error=user_not_found");
            return;
        }

        // 2. Tạo Token
        String token = jwtUtils.generateToken(user);

        // 3. TẠO COOKIE ĐỒNG BỘ VỚI AUTHSERVICE (QUAN TRỌNG)
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Nếu chạy localhost không có https thì tạm set false, nhưng BE ông đang để
        // true nên tôi để true
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "");
        response.addCookie(cookie);

        // 4. Chuyển hướng về Frontend kèm theo các param để Redux xử lý
        String targetUrl = UriComponentsBuilder.fromUriString("https://mystictarots.xyz/oauth2/redirect")
                .queryParam("token", token)
                .queryParam("fullName", user.getFullName())
                .queryParam("email", user.getEmail())
                .queryParam("role", user.getRole().name()) // Thêm .name() để tránh lỗi parse enum
                .queryParam("id", user.getId())
                .queryParam("birthDate", user.getBirthDate())
                .queryParam("profilePicture", user.getProfilePicture())
                .encode()
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
