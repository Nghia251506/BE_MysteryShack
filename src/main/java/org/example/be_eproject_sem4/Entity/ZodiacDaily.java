package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "zodiac_dailies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZodiacDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nameVi; // Tên tiếng Việt: Bạch Dương, Kim Ngưu...

    @Column(nullable = false, unique = true, length = 50)
    private String nameEn; // Aries, Taurus...

    @Column(nullable = false)
    private LocalDate startDate; // Ngày bắt đầu (ví dụ: 21/03)

    @Column(nullable = false)
    private LocalDate endDate; // Ngày kết thúc (ví dụ: 19/04)

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chung về cung

    @Column(length = 255)
    private String imageUrl; // Link ảnh biểu tượng cung

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDate updatedAt = LocalDate.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
