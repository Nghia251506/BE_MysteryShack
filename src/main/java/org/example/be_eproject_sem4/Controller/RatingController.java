package org.example.be_eproject_sem4.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Rate.CreateRatingDTO;
import org.example.be_eproject_sem4.Dto.Rate.RatingResponseDTO;
import org.example.be_eproject_sem4.Dto.Rate.ReaderStatsDTO;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Service.Rating.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Rating & Elo System", description = "Quản lý đánh giá, thống kê và tính toán Elo cho Reader")
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "Gửi đánh giá mới", description = "Tạo đánh giá, cập nhật trạng thái session, tính lại Elo cho Reader và cập nhật stats tháng.")
    @PostMapping("/create")
    public ResponseEntity<RatingResponseDTO> createRating(@RequestBody CreateRatingDTO dto, HttpServletRequest request) {
        // Gọi Service tự xử lý việc móc ID từ Cookie ra luôn
        Long customerId = ratingService.getCustomerIdFromRequest(request);

        return ResponseEntity.ok(ratingService.createRating(dto, customerId));
    }

    @Operation(summary = "Lấy tất cả đánh giá của 1 Reader", description = "Trả về danh sách đánh giá đầy đủ, sắp xếp theo thời gian mới nhất.")
    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<RatingResponseDTO>> getAllByReader(@PathVariable Long readerId) {
        return ResponseEntity.ok(ratingService.getAllRatingsByReader(readerId));
    }

    @Operation(summary = "Bộ lọc đánh giá", description = "Lọc đánh giá theo số sao hoặc chỉ lấy những đánh giá có bình luận.")
    @GetMapping("/reader/{readerId}/filter")
    public ResponseEntity<List<RatingResponseDTO>> filterRatings(
            @PathVariable Long readerId,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false, defaultValue = "false") boolean onlyHasComment) {
        return ResponseEntity.ok(ratingService.filterRatings(readerId, stars, onlyHasComment));
    }

    @Operation(summary = "Lấy chỉ số thống kê của Reader", description = "Lấy trung bình sao và tổng số lượt đánh giá trong tháng hiện tại.")
    @GetMapping("/reader/{readerId}/stats")
    public ResponseEntity<ReaderStatsDTO> getReaderStats(@PathVariable Long readerId) {
        return ResponseEntity.ok(ratingService.getReaderStats(readerId));
    }

    @Operation(summary = "Danh sách phiên chưa đánh giá", description = "Lấy danh sách các Reading Session đã hoàn thành nhưng khách hàng chưa rate.")
    @GetMapping("/pending")
    public ResponseEntity<List<ReadingSession>> getPendingRatings(HttpServletRequest request) {
        // Gọi hàm bốc ID từ Cookie mà ông đã viết
        Long currentCustomerId = ratingService.getCustomerIdFromRequest(request);

        // Giờ nó sẽ tìm đúng data của THẰNG ĐANG ĐĂNG NHẬP
        return ResponseEntity.ok(ratingService.getPendingRatings(currentCustomerId));
    }
}