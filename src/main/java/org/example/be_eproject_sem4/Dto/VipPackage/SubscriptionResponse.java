package org.example.be_eproject_sem4.Dto.VipPackage;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.example.be_eproject_sem4.Entity.VipPackage;

@Data
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private String username;
    private String fullName;
    private String packageName;
    private VipPackage packages;
    private BigDecimal price;
    private Instant startDate;
    private Instant endDate;
    private Integer remainingJobs;
    private String status;
}