package org.example.be_eproject_sem4.Controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.example.be_eproject_sem4.Dto.Auth.UpdateProfileRequest;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam User.Role role,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer minElo,
            @RequestParam(required = false) Integer maxElo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(userService.getAdvancedSearch(
                role, keyword, isActive, startDate, endDate, minElo, maxElo, page, size, sortBy, direction));
    }
}
