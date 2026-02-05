package org.example.be_eproject_sem4.Dto.VipPackage;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VipPackageDto {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer durationDays;
    private String benefits;
    private Integer maxJobsPerDay;
    private String status = "ACTIVE"; 
    private Integer soldCount;
}
