package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tarot_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarotCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false)
    private Integer cardNumber;  // Số thứ tự: 0 (The Fool), 1, 2...

    @Column(name = "name_en", nullable = false, length = 255)
    private String nameEn;       // Tên bài (Tiếng Anh): "The Fool"

    @Column(name = "name_vi", length = 255)
    private String nameVi;       // Tên bài (Tiếng Việt): "Kẻ Khờ"

    @Enumerated(EnumType.STRING)
    @Column(name = "arcana", nullable = false)
    private Arcana arcana;       // Major Arcana / Minor Arcana

    @Column(name = "suit", length = 50)
    private String suit;         // Suit (nếu Minor): Wands, Cups, Swords, Pentacles hoặc "Không có"

    @Column(name = "image_url", length = 500)
    private String imageUrl;     // Link ảnh lá bài

    @Column(name = "upright_meaning", columnDefinition = "TEXT")
    private String uprightMeaning;  // Ý nghĩa chính (upright)

    @Column(name = "reversed_meaning", columnDefinition = "TEXT")
    private String reversedMeaning; // Ý nghĩa nghịch (reversed)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;     // Mô tả chi tiết về lá bài
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    private boolean isActive = true;  // Có hiển thị hay không
    

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}

