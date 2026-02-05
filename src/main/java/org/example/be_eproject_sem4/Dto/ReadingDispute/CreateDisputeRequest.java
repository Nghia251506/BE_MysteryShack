package org.example.be_eproject_sem4.Dto.ReadingDispute;

import lombok.Data;
import java.util.List;

@Data
public class CreateDisputeRequest {
    private Long sessionId; // ID của phiên muốn kiện
    private String reason;   // Lý do: "Reader giải sai", "Thái độ lồi lõm",...
    private List<String> evidenceImages; // List URL ảnh bằng chứng (đã upload lên Cloud/Firebase)
}
