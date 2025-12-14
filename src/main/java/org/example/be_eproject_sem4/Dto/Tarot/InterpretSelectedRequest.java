package org.example.be_eproject_sem4.Dto.Tarot;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterpretSelectedRequest {
    private String topic;
    private LocalDate birthday;

    // 3 lá khách chọn từ FE
    private List<SelectedCard> selectedCards;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SelectedCard {
        private Long cardId;        // ID lá bài
        private boolean isReversed; // FE tự random khi khách lật lá
    }
}
