package org.example.be_eproject_sem4.Entity;
import jakarta.persistence.*;


import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "guest_accesses")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ipAddress;

    @Column
    private int accessCount = 0; // Số lần hỏi

    @Column
    private LocalDate lastAccessDate; // Để reset count hàng ngày nếu cần

    @Column
    private boolean blocked = false; // Chặn IP nếu accessCount > limit
}
