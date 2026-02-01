package org.example.be_eproject_sem4.Dto;

import org.example.be_eproject_sem4.Entity.User;

import lombok.Data;

@Data
public class InterpretationSubmitDto {
    private String interpretation1;
    private String interpretation2;
    private String interpretation3;
    private String advice;
    private User qrPayment;
}
