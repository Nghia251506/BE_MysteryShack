package org.example.be_eproject_sem4.Entity;

import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name= "histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Nullable nếu guest
    private User user;

    @Column
    private String ipAddress; // Lưu IP nếu guest

    @Column(nullable = false)
    private String type; // "ZODIAC" hoặc "TAROT"

    @Column
    private String zodiacSign; // Nếu type=ZODIAC, ví dụ: "Aries"

    @Column(columnDefinition = "TEXT")
    private String result; // Kết quả chi tiết (JSON hoặc text)

    @Column
    private LocalDateTime viewedAt; // Thời gian xem
}
