package org.example.be_eproject_sem4.Dto.ReadingDispute;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

@Data
public class DisputeResponse {
    private Long id;
    private Long sessionId;
    
    // Khách hàng (Người kiện)
    private Long customerId;
    private String customerName;
    private String customerEmail;
    
    // Reader (Người bị kiện)
    private Long readerId;
    private String readerName;
    
    // Nội dung khiếu nại
    private String reason;
    private List<String> evidenceImages;
    private String status; // PENDING, RESOLVED_REFUND, RESOLVED_REJECT
    
    // Thông tin tài chính & Phiên
    private BigDecimal amount; // Số tiền của phiên đó
    private String topicName;  // Chủ đề: Tình yêu, Sự nghiệp...
    private String question; // Câu hỏi của khách
    
    // Log xử lý
    private String adminNote;
    private Instant createdAt;
    private Instant resolvedAt;
}
