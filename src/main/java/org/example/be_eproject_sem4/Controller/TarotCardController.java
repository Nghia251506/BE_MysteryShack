package org.example.be_eproject_sem4.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.TarotCard.CreateTarotCardDto;
import org.example.be_eproject_sem4.Dto.TarotCard.TarotCardResponseDto;
import org.example.be_eproject_sem4.Dto.TarotCard.UpdateTarotCardDto;
import org.example.be_eproject_sem4.Entity.ApiResponse;
import org.example.be_eproject_sem4.Service.TarotCard.TarotCardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarot-cards")
@RequiredArgsConstructor
@Tag(name = "Tarot Card Management", description = "Quản lý lá bài Tarot (Admin) và API công khai")
@SecurityRequirement(name = "cookieAuth")  // Cho Swagger hiển thị nút Authorize
public class TarotCardController {

    private final TarotCardService tarotCardService;

    // ==================== ADMIN CRUD ====================

    @Operation(summary = "Lấy danh sách lá bài (phân trang) - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAllCardsAdmin(
            @Parameter(description = "Trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sắp xếp theo field (ví dụ: cardNumber,asc)") 
            @RequestParam(defaultValue = "cardNumber,asc") String sort,
            @RequestParam(required = false) String arcana, // Thêm lọc Arcana
            @RequestParam(required = false) String suit
        ) 
        {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams.length > 1 ? sortParams[1] : "asc");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<TarotCardResponseDto> cardPage = tarotCardService.getAllCards(pageable, arcana, suit);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lấy danh sách lá bài thành công");
        response.put("data", cardPage.getContent());
        response.put("currentPage", cardPage.getNumber());
        response.put("totalItems", cardPage.getTotalElements());
        response.put("totalPages", cardPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy lá bài theo ID - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> getCardByIdAdmin(@PathVariable Long id) {
        TarotCardResponseDto card = tarotCardService.getCardById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Lấy lá bài thành công", card));
    }

    @Operation(summary = "Tạo lá bài mới - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse> createCard(@Valid @RequestBody CreateTarotCardDto dto) {
        TarotCardResponseDto created = tarotCardService.createCard(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Tạo lá bài thành công", created));
    }

    @Operation(summary = "Cập nhật lá bài - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> updateCard(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateTarotCardDto dto) {
        TarotCardResponseDto updated = tarotCardService.updateCard(id, dto);
        return ResponseEntity.ok(new ApiResponse(true, "Cập nhật lá bài thành công", updated));
    }

    @Operation(summary = "Xóa mềm lá bài (ẩn) - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/soft/{id}")
    public ResponseEntity<ApiResponse> softDeleteCard(@PathVariable Long id) {
        tarotCardService.softDeleteCard(id);
        return ResponseEntity.ok(new ApiResponse(true, "Ẩn lá bài thành công"));
    }

    @Operation(summary = "Xóa cứng lá bài - Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> deleteCard(@PathVariable Long id) {
        tarotCardService.deleteCard(id);
        return ResponseEntity.ok(new ApiResponse(true, "Xóa lá bài thành công"));
    }

    // ==================== PUBLIC API (User + Guest) ====================

    @Operation(summary = "Lấy tất cả lá bài active - Public (dùng cho rút bài)")
    @GetMapping("/public/active")
    public ResponseEntity<ApiResponse> getAllActiveCards() {
        List<TarotCardResponseDto> cards = tarotCardService.getAllActiveCards();
        return ResponseEntity.ok(new ApiResponse(true, "Lấy danh sách lá bài thành công", cards));
    }

    @Operation(summary = "Tìm kiếm lá bài active theo từ khóa - Public")
    @GetMapping("/public/search")
    public ResponseEntity<ApiResponse> searchActiveCards(
            @Parameter(description = "Từ khóa tìm kiếm (nameEn, nameVi, keyword)")
            @RequestParam(required = false) String query) {
        List<TarotCardResponseDto> cards = tarotCardService.searchActiveCards(query);
        return ResponseEntity.ok(new ApiResponse(true, "Tìm kiếm thành công", cards));
    }

    @Operation(summary = "Lấy lá bài theo số thứ tự - Public")
    @GetMapping("/public/number/{cardNumber}")
    public ResponseEntity<ApiResponse> getCardByNumber(@PathVariable Integer cardNumber) {
        TarotCardResponseDto card = tarotCardService.getCardByCardNumber(cardNumber);
        return ResponseEntity.ok(new ApiResponse(true, "Lấy lá bài thành công", card));
    }

    @Operation(summary = "Lấy lá bài theo tên tiếng Anh - Public")
    @GetMapping("/public/name/{nameEn}")
    public ResponseEntity<ApiResponse> getCardByNameEn(@PathVariable String nameEn) {
        TarotCardResponseDto card = tarotCardService.getCardByNameEn(nameEn);
        return ResponseEntity.ok(new ApiResponse(true, "Lấy lá bài thành công", card));
    }

    @Operation(summary = "Đếm tổng số lá bài active - Public")
    @GetMapping("/public/count")
    public ResponseEntity<ApiResponse> countActiveCards() {
        long count = tarotCardService.countActiveCards();
        Map<String, Long> result = Map.of("totalActiveCards", count);
        return ResponseEntity.ok(new ApiResponse(true, "Đếm thành công", result));
    }
}