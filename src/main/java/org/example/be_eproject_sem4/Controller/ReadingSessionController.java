package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.ReadingSessionDTO;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Service.Request.ReadingSessionService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@CrossOrigin(origins = "*")
public class ReadingSessionController {

    @Autowired
    private ReadingSessionService sessionService;

    // 1. Lấy toàn bộ lịch sử các phiên đọc
    @GetMapping
    public ResponseEntity<Page<ReadingSession>> getAllSessions(
            @RequestParam(required = false) String tab,
            @ParameterObject Pageable pageable) { // Thêm cái này để Swagger hiện ô nhập chuẩn
        return ResponseEntity.ok(sessionService.getAllSessions(tab, null, pageable));
    }

    // 2. Lấy chi tiết một phiên đọc theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ReadingSession> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    // 3. Khởi tạo một phiên đọc bài mới (POST)
    @PostMapping
    public ResponseEntity<ReadingSession> createSession(@RequestBody ReadingSessionDTO sessionDTO) {
        ReadingSession createdSession = sessionService.createSession(sessionDTO);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    // 4. Cập nhật kết quả phiên đọc (Thường dùng sau khi đã chọn bài và có luận
    // giải)
    @PutMapping("/{id}")
    public ResponseEntity<ReadingSession> updateSession(
            @PathVariable Long id,
            @RequestBody ReadingSessionDTO sessionDTO) {
        return ResponseEntity.ok(sessionService.updateSession(id, sessionDTO));
    }

    // 5. Xóa phiên đọc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Reader xem list request đã matched với mình
    @GetMapping("/total-income")
    public ResponseEntity<BigDecimal> getTotalIncome() {
        // Gọi hàm Service mình vừa viết ở bước trước
        BigDecimal total = sessionService.getTotalIncomeForReader();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total-sessions")
    public ResponseEntity<Long> getTotalSessions() {
        return ResponseEntity.ok(sessionService.getTotalCompletedSessionsForReader());
    }

    @GetMapping("/reader/{readerId}")
    public ResponseEntity<ReadingSession> getAllSessionsByReader(@PathVariable Long readerId) {
        return ResponseEntity.ok(sessionService.getLatestProcessingSession(readerId));
    }

    // @GetMapping("/customer")
    // @PreAuthorize("hasRole('CUSTOMER')")
    // public ResponseEntity<List> getReadingSessionsForCustomer() {
    // return ResponseEntity.ok(sessionService.getMatchedSessionsForReader());
    // }

    // 7. Reader accept request
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<String> acceptSession(@PathVariable Long id) {
        sessionService.acceptSession(id);
        return ResponseEntity.ok("Đã chấp nhận request #" + id);
    }

    // 8. Reader reject request → chuyển về PENDING để tìm reader khác
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<String> rejectSession(@PathVariable Long id) {
        sessionService.rejectSession(id);
        return ResponseEntity.ok("Đã từ chối request #" + id + ", hệ thống đang tìm reader mới");
    }
}
