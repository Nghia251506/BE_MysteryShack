package org.example.be_eproject_sem4.Dto.Chart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformancePoint {
    private String name;  // VD: "COMPLETED", "CANCELLED"
    private Long value;
    private String color; // VD: "#f59e0b"
}