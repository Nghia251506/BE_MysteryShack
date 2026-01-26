package org.example.be_eproject_sem4.Dto;

import org.example.be_eproject_sem4.Entity.InterpretationStatus;

import lombok.Data;

@Data
public class InterpretationResponseDto {
    private Long id;
    private String interpretation1; // Khách luôn thấy
    private String interpretation2; // Ẩn nếu chưa PAID
    private String interpretation3; // Ẩn nếu chưa PAID
    private String advice;          // Ẩn nếu chưa PAID
    private String qrPayment;       // Khách thấy để quét
    private InterpretationStatus status;
    private Long sessionId;
}
