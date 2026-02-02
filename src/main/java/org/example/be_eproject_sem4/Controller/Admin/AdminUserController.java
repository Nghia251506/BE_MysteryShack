package org.example.be_eproject_sem4.Controller.Admin;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // Lấy danh sách READER có phân trang
    @GetMapping("/readers")
    public ResponseEntity<Page<User>> getAllReaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy, // Mặc định xếp theo tên
            @RequestParam(defaultValue = "desc") String direction) {

        Page<User> readers = userService.getUsersByRole(User.Role.READER, page, size, sortBy, direction);
        return ResponseEntity.ok(readers);
    }

    @GetMapping("/customers")
    public ResponseEntity<Page<User>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<User> customers = userService.getUsersByRole(User.Role.CUSTOMER, page, size, sortBy, direction);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam User.Role role,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
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
