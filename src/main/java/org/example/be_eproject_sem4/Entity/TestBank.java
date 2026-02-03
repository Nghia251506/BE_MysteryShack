package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Entity
@Table(name = "test_banks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    // Lưu danh sách đáp án dưới dạng một bảng phụ (Collection)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "test_bank_options", // Tên bảng phụ
            joinColumns = @JoinColumn(name = "test_bank_id") // Đặt tên cột liên kết rõ ràng
    )
    @Column(name = "option_text")
    @OrderColumn(name = "option_order")
    private List<String> options;

    private String correctAnswer;

    @Enumerated(EnumType.STRING)
    private QuestionCategory category;
}
