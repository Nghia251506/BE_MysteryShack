package org.example.be_eproject_sem4.Dto;

import java.math.BigDecimal;
import java.util.List;

import org.example.be_eproject_sem4.Entity.InterpretationStatus;

import lombok.Data;

@Data
public class InterpretationResponseDto {
    private Long id;
    private String interpretation1;
    private String interpretation2;
    private String interpretation3;
    private String advice;
    private String qrPayment;
    private InterpretationStatus status;
    private Long sessionId;

    // 1. Thông tin Reader
    private ReaderInfo reader;

    // 2. Thông tin câu hỏi
    private String questionContent;
    private BigDecimal amount;

    // 3. Danh sách lá bài
    private List<SelectedCardDto> selectedCards;

    @Data
    public static class ReaderInfo {
        private Long id;
        private String fullName;
        private String avatar;
    }
}
