package org.example.be_eproject_sem4.Controller;

import java.util.List;

import org.example.be_eproject_sem4.Entity.History;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.HistoryRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final org.example.be_eproject_sem4.Service.History.HistoryService historyService;

    /**
     * 1. GET /api/v1/histories/my-history Dành cho CUSTOMER: Xem toàn bộ lịch
     * sử xem bói của chính mình
     */
    @GetMapping("/my-history")
    public ResponseEntity<Page<History>> getMyHistory(
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User currentUser = getCurrentUser();
        Page<History> histories;

        // Kiểm tra Role (Giả sử ông dùng String hoặc Enum cho Role)
        // Nếu User có role là READER thì lấy lịch sử họ đi giải bài
        if (currentUser.getRole().name().equals("READER")) {
            histories = historyRepository.findByReaderId(currentUser.getId(), pageable);
        } else {
            // Mặc định là khách hàng đi xem bài
            histories = historyRepository.findByCustomerId(currentUser.getId(), pageable);
        }

        return ResponseEntity.ok(histories);
    }

    /**
     * 2. GET /api/v1/histories/reader-jobs Dành cho READER: Xem danh sách các
     * đơn hàng đã nhận/được giao
     */
    @GetMapping("/reader-jobs")
    public ResponseEntity<?> getReaderJobs() {
        User currentUser = getCurrentUser();

        if (!User.Role.READER.equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body("Chỉ Reader mới có quyền truy cập danh sách này.");
        }

        // Lấy lịch sử theo Reader ID
        List<History> jobs = historyRepository.findByReaderIdOrderByCreatedAtDesc(currentUser.getId());
        return ResponseEntity.ok(jobs);
    }

    /**
     * 3. GET /api/v1/histories/{id} Xem chi tiết một bản ghi History. Bảo mật:
     * Chỉ Customer sở hữu đơn HOẶC Reader nhận đơn đó mới xem được.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHistoryDetail(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        History history = historyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử với ID: " + id));

        // --- CHECK QUYỀN TRUY CẬP ---
        boolean isOwner = history.getCustomer().getId().equals(currentUser.getId());
        boolean isAssignedReader = history.getReader() != null
                && history.getReader().getId().equals(currentUser.getId());

        if (!isOwner && !isAssignedReader) {
            return ResponseEntity.status(403).body("Bạn không có quyền xem chi tiết đơn hàng này.");
        }

        return ResponseEntity.ok(history);
    }

    // ==========================================
    // Hàm phụ trợ: Lấy User đang đăng nhập từ Token
    // ==========================================
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại hoặc chưa đăng nhập"));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getHistoryBySessionId(@PathVariable Long sessionId) {
        User currentUser = getCurrentUser();

        // Tìm History dựa vào sessionId (request_id trong DB)
        // Lưu ý: Đảm bảo HistoryRepository đã có hàm findByRequestId
        History history = historyRepository.findByRequestId(sessionId)
                .orElseThrow(() -> new RuntimeException(
                "Chưa tìm thấy dữ liệu lịch sử cho phiên này (Session ID: " + sessionId + ")"));

        // --- CHECK QUYỀN (SECURITY) ---
        // 1. Là khách hàng của đơn này
        boolean isOwner = history.getCustomer().getId().equals(currentUser.getId());

        // 2. Là Reader được gán cho đơn này (cần check null vì lúc đầu có thể chưa có
        // Reader)
        boolean isAssignedReader = history.getReader() != null
                && history.getReader().getId().equals(currentUser.getId());

        if (!isOwner && !isAssignedReader) {
            return ResponseEntity.status(403).body("Bạn không có quyền xem lịch sử của phiên này.");
        }

        return ResponseEntity.ok(history);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = getCurrentUser(); // Hàm lấy user từ SecurityContext của bạn

        Page<History> recentHistories = historyService.getRecentHistory(currentUser, page, size);

        return ResponseEntity.ok(recentHistories);
    }
}
