package org.example.be_eproject_sem4.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.example.be_eproject_sem4.Dto.Auth.UpdateProfileRequest;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.example.be_eproject_sem4.Service.Rating.RatingService;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RatingService jwtTokenProvider;
    @Autowired
    private org.example.be_eproject_sem4.Repository.UserRepository userRepository;

    /**
     * Lấy Reader ngẫu nhiên từ Top Elo.
     * Hỗ trợ query parameter 'excludeId' để khi nhấn "Đổi Reader" trên FE không bị
     * trùng người cũ.
     * Ví dụ: /api/users/readers/random-top?excludeId=8
     */
    @GetMapping("/readers/random-top")
    public ResponseEntity<User> getRandomTopReader(
            @RequestParam(required = false) List<Long> excludeIds,
            HttpServletRequest request // Lấy ID khách từ request cho bảo mật
    ) {
        // 1. Lấy ID khách hàng từ Token/Cookie (Hàm ông đã viết)
        Long customerId = jwtTokenProvider.getCustomerIdFromRequest(request);

        // 2. Gọi Service xử lý trọn gói logic
        User result = userService.findRandomReader(excludeIds, customerId);

        // 3. Trả về kết quả
        return (result != null)
                ? ResponseEntity.ok(result)
                : ResponseEntity.noContent().build();
    }

    // lấy profile chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/toggle-status")
    @Transactional
    public ResponseEntity<?> toggleStatus() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // Đảo trạng thái
        user.setActive(!user.isActive());

        // Lưu và lấy bản ghi mới nhất từ DB
        User updatedUser = userRepository.saveAndFlush(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "newStatus", updatedUser.isActive(),
                "message", "Đã đổi sang " + (updatedUser.isActive() ? "ONLINE" : "OFFL`INE")));
    }

    @PatchMapping("/booking-info/{id}")
    public ResponseEntity<User> patchUserProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        User updated = userService.updateFullnameAndBirthdate(id, request.getFullName(), request.getBirthDate());
        return ResponseEntity.ok(updated);
    }
}
