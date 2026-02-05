package org.example.be_eproject_sem4.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.example.be_eproject_sem4.Dto.Auth.UpdateProfileRequest;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Dto.Auth.UserUpdateDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.example.be_eproject_sem4.Service.Rating.RatingService;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody UserUpdateDto dto) {
        User updated = userService.updateProfile(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/admin/getall")
    public ResponseEntity<List<User>> getAllReader() {
        List<User> users = userRepository.findAllReader();
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<UserDto> customers = userService.getAllCustomers(keyword, pageable);
        return ResponseEntity.ok(customers);
    }

    // 2. Lấy chi tiết 1 khách hàng (Bao gồm cả lịch sử matchedSessions)
    @GetMapping("/admin/{id}")
    public ResponseEntity<UserDto> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getCustomerDetail(id));
    }

    // 3. Khóa/Mở khóa tài khoản khách hàng
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserDto> toggleStatus(@PathVariable Long id) {
        UserDto updatedUser = userService.toggleCustomerStatus(id);
        return ResponseEntity.ok(updatedUser);
    }
}
