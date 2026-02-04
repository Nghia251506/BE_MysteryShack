package org.example.be_eproject_sem4.Dto;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
public class PaymentDTO {
    private Long id;
    private String transactionNo;
    private Long amount;
    private String bankCode;
    private String orderInfo;
    private LocalDateTime paymentDate;
    private String status;
    private String username; // Tên Reader mua gói
    private String packageName; // Tên gói VIP
}