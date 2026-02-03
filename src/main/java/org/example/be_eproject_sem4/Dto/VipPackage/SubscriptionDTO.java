package org.example.be_eproject_sem4.Dto.VipPackage;

import lombok.Data;

import java.time.Instant;

@Data
public class SubscriptionDTO {
    private Long id;
    private Long readerId;
    private String readerFullName;
    private Integer packageId;
    private String packageName;
    private Instant startDate;
    private Instant endDate;
    private String status;
    private Integer remainingJobs; // Tổng lượt còn lại của gói
}
