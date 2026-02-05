package org.example.be_eproject_sem4.Dto.ReadingDispute;

import lombok.Data;

@Data
public class ResolveDisputeRequest {
    // RESOLVED_REFUND (Hoàn tiền), RESOLVED_REJECT (Bác bỏ khiếu nại)
    private String status; 
    
    // Ghi chú của Admin về lý do xử lý để lưu lại đối soát sau này
    private String adminNote; 
}
