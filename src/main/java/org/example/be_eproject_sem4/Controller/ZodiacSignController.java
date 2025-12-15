package org.example.be_eproject_sem4.Controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacAskRequestDto;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacDailyRequestDto;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacSignResponseDto;
import org.example.be_eproject_sem4.Entity.ApiResponse;
import org.example.be_eproject_sem4.Service.AI.AiInterpretationService;
import org.example.be_eproject_sem4.Service.Zodiac.ZodiacSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/zodiac")
@CrossOrigin(origins = "*")
public class ZodiacSignController {

    @Autowired
    private ZodiacSignService zodiacSignService;
    @Autowired
    private AiInterpretationService aiService;

    // ==================== PUBLIC API ====================

    // Lấy tất cả cung active (cho client dùng)
    @GetMapping("/active")
    public ResponseEntity<List<ZodiacSignResponseDto>> getActiveZodiacSigns() {
        return ResponseEntity.ok(zodiacSignService.getActiveZodiacSigns());
    }

    // Lấy cung theo ngày sinh (rất quan trọng cho tính cung từ birthday)
    @GetMapping("/birth-date")
    public ResponseEntity<ZodiacSignResponseDto> getZodiacByBirthDate(@RequestParam("date") String dateStr) {
        LocalDate birthDate = LocalDate.parse(dateStr); // format yyyy-MM-dd
        return ResponseEntity.ok(zodiacSignService.getZodiacSignByBirthDate(birthDate));
    }

    // Lấy cung theo tên tiếng Việt
    @GetMapping("/name-vi/{nameVi}")
    public ResponseEntity<ZodiacSignResponseDto> getZodiacByNameVi(@PathVariable String nameVi) {
        return ResponseEntity.ok(zodiacSignService.getZodiacSignByNameVi(nameVi));
    }

    // Lấy cung theo tên tiếng Anh
    @GetMapping("/name-en/{nameEn}")
    public ResponseEntity<ZodiacSignResponseDto> getZodiacByNameEn(@PathVariable String nameEn) {
        return ResponseEntity.ok(zodiacSignService.getZodiacSignByNameEn(nameEn));
    }

    // Lấy cung theo ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<ZodiacSignResponseDto> getZodiacById(@PathVariable Long id) {
        return ResponseEntity.ok(zodiacSignService.getZodiacSignById(id));
    }
    @Operation(summary = "Hỏi AI về cung hoàng đạo theo form khách nhập", description = "Khách nhập tên, ngày sinh, giới tính → backend tính cung → AI generate câu trả lời theo các phần: Tổng quan, Điểm mạnh, Điểm yếu, Tính cách, Gia đình, Tình yêu, Tình dục, Sự Nghiệp")
    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> askZodiacAI(@Valid @RequestBody ZodiacAskRequestDto requestDto) {

        // Tính cung hoàng đạo từ ngày sinh
        ZodiacSignResponseDto zodiac = zodiacSignService.getZodiacSignByBirthDate(requestDto.getBirthday());

        // Gọi AI generate câu trả lời cho cung hoàng đạo
        String aiResponse = aiService.generateZodiacResponse(
                requestDto.getName(),
                requestDto.getGender(),
                requestDto.getBirthday(),
                zodiac.getNameVi()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Trả lời từ AI về cung hoàng đạo thành công", aiResponse));
    }

    // ==================== ADMIN API ====================

    // Lấy tất cả cung (bao gồm inactive – cho admin quản lý)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ZodiacSignResponseDto>> getAllZodiacSignsAdmin() {
        return ResponseEntity.ok(zodiacSignService.getAllZodiacSigns());
    }

    // Tạo mới cung
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZodiacSignResponseDto> createZodiacSign(@Valid @RequestBody ZodiacDailyRequestDto requestDto) {
        return ResponseEntity.ok(zodiacSignService.createZodiacSign(requestDto));
    }

    // Cập nhật cung
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZodiacSignResponseDto> updateZodiacSign(
            @PathVariable Long id,
            @Valid @RequestBody ZodiacDailyRequestDto requestDto) {
        return ResponseEntity.ok(zodiacSignService.updateZodiacSign(id, requestDto));
    }

    // Xóa mềm cung (set active = false)
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteZodiacSign(@PathVariable Long id) {
        zodiacSignService.deleteZodiacSign(id);
        return ResponseEntity.noContent().build();
    }
}