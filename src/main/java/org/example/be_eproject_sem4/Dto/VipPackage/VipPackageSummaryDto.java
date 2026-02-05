package org.example.be_eproject_sem4.Dto.VipPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor // BẮT BUỘC phải có cái này để câu Query "SELECT new..." trong Repo chạy được
@NoArgsConstructor
public class VipPackageSummaryDto {
    private long totalPackages;      // Tổng số gói
    private long activePackages;     // Số gói đang bán
    private long totalSoldCount;     // Tổng số lượt đã bán
    private BigDecimal totalRevenue; // Tổng doanh thu (Price * SoldCount)
}
