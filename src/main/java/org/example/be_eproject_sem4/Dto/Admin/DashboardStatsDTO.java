package org.example.be_eproject_sem4.Dto.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    // 1. Tổng số phiên trong ngày hôm nay
    private long totalSessionsToday;
    
    // 2. Số phiên đang ở trạng thái Đang diễn ra (IN_PROGRESS)
    private long activeSessions;
    
    // 3. Số lượng Reader đang Online/Sẵn sàng
    private long onlineReaders;
    
    // 4. Số khách hàng mới (đăng ký trong 24h qua)
    private long newCustomersToday;

    // 5. Tỷ lệ tăng trưởng (để hiển thị cái +15% màu xanh trên UI)
    private double sessionGrowth;
    private double activeGrowth;
    private double readerGrowth;
    private double customerGrowth;
}
