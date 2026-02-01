package org.example.be_eproject_sem4.Dto;

import com.google.type.Decimal;
import org.example.be_eproject_sem4.Entity.User;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InterpretationSubmitDto {
    private String interpretation1;
    private String interpretation2;
    private String interpretation3;
    private String advice;
    private User qrPayment;
    private BigDecimal amount;
}
