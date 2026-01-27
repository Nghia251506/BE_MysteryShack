package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Lấy Reader ngẫu nhiên từ Top Elo.
     * Hỗ trợ query parameter 'excludeId' để khi nhấn "Đổi Reader" trên FE không bị trùng người cũ.
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
}
