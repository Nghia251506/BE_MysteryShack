package org.example.be_eproject_sem4.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"passwordHash"}) // Không show password khi toString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;
    @Column(name="birthday")
    private Date birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    // Dành riêng cho Reader
    @Column(name = "elo_score", nullable = false)
    private int eloScore = 1000;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enum cho role
    public enum Role {
        CUSTOMER, READER
    }

    // Helper method để check role (tùy chọn)
    public boolean isReader() {
        return this.role == Role.READER;
    }

    public boolean isCustomer() {
        return this.role == Role.CUSTOMER;
    }
}