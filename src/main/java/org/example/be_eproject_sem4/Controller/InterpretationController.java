package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.InterpretationResponseDto;
import org.example.be_eproject_sem4.Dto.InterpretationSubmitDto;
import org.example.be_eproject_sem4.Service.Interpretation.InterpretationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/interpretations")
@RequiredArgsConstructor
public class InterpretationController {
    private final InterpretationService interpretationService;

    // 1. Reader nộp bài + ảnh QR
    @PostMapping("/submit/{sessionId}")
    public ResponseEntity<InterpretationResponseDto> submit(@PathVariable Long sessionId,
            @RequestBody InterpretationSubmitDto dto) {
        return ResponseEntity.ok(interpretationService.submitInterpretation(sessionId, dto));
    }

    // 2. Reader xác nhận đã nhận tiền (Khi thấy thông báo ngân hàng)
    @PostMapping("/confirm-payment/{sessionId}")
    public ResponseEntity<String> confirm(@PathVariable Long sessionId) {
        interpretationService.confirmPayment(sessionId);
        return ResponseEntity.ok("Xác nhận thành công! Khách hàng hiện đã xem được bài giải đầy đủ.");
    }

    @GetMapping("/customer/view/{sessionId}")
    public ResponseEntity<InterpretationResponseDto> getInterpretationForCustomer(@PathVariable Long sessionId) {
        InterpretationResponseDto response = interpretationService.getForCustomer(sessionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/notify-paid")
    public ResponseEntity<?> notifyPaid(@PathVariable Long sessionId) {
        interpretationService.customerNotifyPaid(sessionId);
        return ResponseEntity.ok("Đã gửi thông báo thanh toán cho Reader.");
    }
}
