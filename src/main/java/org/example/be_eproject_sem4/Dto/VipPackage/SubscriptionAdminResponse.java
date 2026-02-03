package org.example.be_eproject_sem4.Dto.VipPackage;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class SubscriptionAdminResponse {
    private Long id;
    private String username;
    private String fullName;
    private String packageName;
    private BigDecimal price;
    private Instant startDate;
    private Instant endDate;
    private Integer remainingJobs;
    private String status;
}
