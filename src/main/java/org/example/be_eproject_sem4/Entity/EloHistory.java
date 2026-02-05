package org.example.be_eproject_sem4.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elo_histories")
@Data
public class EloHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long readerId;
    private Integer changeAmount;
    private Integer beforeElo;
    private Integer afterElo;
    private String reason;
    private String type;
    
    @CreatedBy
    private String adminUsername; // Ai là người thực hiện
    
    @CreatedDate
    private LocalDateTime createdAt;
}
