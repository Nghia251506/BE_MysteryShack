package org.example.be_eproject_sem4.Controller;

import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Auth.*;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.example.be_eproject_sem4.Config.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDto req) {
        UserDto createdUser = userService.create(req);  // Service lo hết: kiểm tra trùng, encode pass, gán role CLIENT

        return ResponseEntity.ok(new ApiResponse());
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginDto req,
                                             HttpServletResponse response) {
        // Xác thực username + password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy UserDetails từ authentication (đã được load từ UserDetailsService)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Tạo JWT từ UserDetails
        String jwt = jwtService.generateToken(userDetails);
        ResponseCookie cookie = jwtService.generateCookie(jwt);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse());
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            MeResponse error = MeResponse.builder()
                    .success(false)
                    .message("Chưa đăng nhập hoặc token hết hạn")
                    .build();
            return ResponseEntity.status(401).body(error);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Lấy fullname từ Service (chỉ lấy những gì cần, không lộ Entity)
        String fullname = userService.getFullNameByUsername(username);

        Set<String> roles = authentication.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());

        Set<String> permissions = authentication.getAuthorities().stream()
                .filter(a -> !a.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        MeResponse meResponse = MeResponse.builder()
                .success(true)
                .message("Lấy thông tin thành công")
                .username(username)
                .fullname(fullname)
                .role(roles.isEmpty() ? "CLIENT" : roles.iterator().next())
                .permissions(permissions)
                .active(userDetails.isEnabled())
                .build();

        return ResponseEntity.ok(meResponse);
    }

    // POST /api/auth/logout (tùy chọn)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        ResponseCookie cookie = jwtService.generateLogoutCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse());
    }
}