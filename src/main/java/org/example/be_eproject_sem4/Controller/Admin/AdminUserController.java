package org.example.be_eproject_sem4.Controller.Admin;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
