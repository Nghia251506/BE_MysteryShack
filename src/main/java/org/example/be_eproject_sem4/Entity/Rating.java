package org.example.be_eproject_sem4.Entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name= "ratings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private ReadingSession request;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;
    @Column(name = "rating", nullable = false)
    private Integer ratingValue;
    @Column(name = "reply_comment", columnDefinition = "LONGTEXT")
    private String replyComment;
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    @CreationTimestamp
    private Instant createdAt;
    private Instant repliedAt;
}
