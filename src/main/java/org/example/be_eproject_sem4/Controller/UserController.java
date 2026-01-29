package org.example.be_eproject_sem4.Controller;

import java.util.Map;

import org.example.be_eproject_sem4.Dto.StatusRequest;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Security.CustomUserDetailsService;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private org.example.be_eproject_sem4.Repository.UserRepository userRepository;

    /**
     * Lấy Reader ngẫu nhiên từ Top Elo.
     * Hỗ trợ query parameter 'excludeId' để khi nhấn "Đổi Reader" trên FE không bị
     * trùng người cũ.
     * Ví dụ: /api/users/readers/random-top?excludeId=8
     */
    @GetMapping("/readers/random-top")
    public ResponseEntity<User> getRandomTopReader(@RequestParam(required = false) Long excludeId) {
        User result;
        if (excludeId != null) {
            result = userService.getRandomTopReaderExcludingMe(excludeId);
        } else {
            result = userService.getRandomTopReader();
        }

        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    // Giữ nguyên để lấy profile chi tiết
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
}
