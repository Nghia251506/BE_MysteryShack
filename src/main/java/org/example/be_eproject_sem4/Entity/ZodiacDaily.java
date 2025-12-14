package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "zodiac_dailies")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZodiacDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sign; // "Aries", "Taurus", etc.

    @Column(nullable = false)
    private LocalDate date; // Ngày cụ thể

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả biến động hành tinh, dự đoán ngày

    @Column(columnDefinition = "TEXT")
    private String planetaryChanges; // Chi tiết hành tinh (JSON nếu phức tạp)

     // Unique constraint để tránh duplicate: sign + date
//    @UniqueConstraint(columnNames = {"sign", "date"})
}
