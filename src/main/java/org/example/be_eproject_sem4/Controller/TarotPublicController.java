package org.example.be_eproject_sem4.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Tarot.InterpretSelectedRequest;
import org.example.be_eproject_sem4.Entity.CommonApiResponse;
import org.example.be_eproject_sem4.Dto.Tarot.DrawTarotRequest;
import org.example.be_eproject_sem4.Dto.Tarot.DrawTarotResponse;
import org.example.be_eproject_sem4.Dto.TarotCard.TarotCardResponseDto;
import org.example.be_eproject_sem4.Service.TarotCard.TarotCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarot")
@RequiredArgsConstructor
@Tag(name = "Tarot Public API", description = "API rút bài Tarot cho khách (guest/user) - có thể test trực tiếp trên Swagger")
public class TarotPublicController {

    private final TarotCardService tarotCardService;

    // @Operation(
    // summary = "Rút 3 lá Tarot theo chủ đề hỏi",
    // description = """
    // Khách gửi chủ đề hỏi → backend tự động:
    // - Lọc bộ bài phù hợp (Major cho câu hỏi lớn, Minor theo suit cho câu hỏi cụ
    // thể)
    // - Xáo bài ngẫu nhiên
    // - Rút 3 lá (random reversed 50%)
    // - Gọi AI giải nghĩa tổng hợp
    // Trả về 3 lá + câu giải nghĩa từ AI
    // """
    // )
    // @ApiResponses(value = {
    // @ApiResponse(responseCode = "200", description = "Rút bài thành công",
    // content = @Content(schema = @Schema(implementation =
    // DrawTarotResponse.class))),
    // @ApiResponse(responseCode = "400", description = "Chủ đề không hợp lệ"),
    // @ApiResponse(responseCode = "500", description = "Lỗi server hoặc AI")
    // })
    @PostMapping("/shuffle")
    public ResponseEntity<CommonApiResponse<List<TarotCardResponseDto>>> shuffle(
            @Valid @RequestBody DrawTarotRequest request) {
        // Truyền nguyên cả object 'request' vào service thay vì chỉ lấy .getTopic()
        List<TarotCardResponseDto> shuffledDeck = tarotCardService.shuffleAndGetDeck(request);

        return ResponseEntity.ok(new CommonApiResponse<>(
                true,
                "Xáo bài thành công cho chủ đề: " + request.getTopic(),
                shuffledDeck));
    }

    @PostMapping("/interpret")
    public ResponseEntity<CommonApiResponse<DrawTarotResponse>> interpret(
            @Valid @RequestBody InterpretSelectedRequest request) {
        DrawTarotResponse result = tarotCardService.interpretSelectedCards(
                request.getTopic(),
                request.getBirthday(),
                request.getSelectedCards());
        return ResponseEntity.ok(new CommonApiResponse<>(true, "Giải nghĩa thành công", result));
    }

    @Operation(summary = "Lấy tất cả lá bài đang active", description = "Dùng cho frontend nếu cần load toàn bộ bộ bài")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @GetMapping("/public/active")
    public ResponseEntity<CommonApiResponse<List<TarotCardResponseDto>>> getAllActiveCards() {
        List<TarotCardResponseDto> cards = tarotCardService.getAllActiveCards();
        return ResponseEntity.ok(new CommonApiResponse<>(true, "Lấy danh sách lá bài thành công", cards));
    }

    @Operation(summary = "Đếm số lá bài active hiện có")
    @GetMapping("/public/count")
    public ResponseEntity<CommonApiResponse<Long>> countActiveCards() {
        long count = tarotCardService.countActiveCards();
        return ResponseEntity.ok(new CommonApiResponse<>(true, "Đếm thành công", count));
    }
}