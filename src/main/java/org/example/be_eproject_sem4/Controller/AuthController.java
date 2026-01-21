package org.example.be_eproject_sem4.Controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Auth.*;
import org.example.be_eproject_sem4.Service.Auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}