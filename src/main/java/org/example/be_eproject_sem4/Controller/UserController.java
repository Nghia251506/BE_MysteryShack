package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/readers/random-top")
    public ResponseEntity<User> getRandomTopReader() {
        User randomReader = userService.getRandomTopReader();

        if (randomReader == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(randomReader);
    }

    @GetMapping("/readers/random-top/{readerId}")
    public ResponseEntity<User> getRandomTopReaderExcludingMe(@PathVariable Long readerId) {
        User result = userService.getRandomTopReaderExcludingMe(readerId);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            // Trả về 404 Not Found nếu không tìm thấy User
            return ResponseEntity.notFound().build();
        }

        // Trả về 200 OK cùng dữ liệu User
        return ResponseEntity.ok(user);
    }
}
