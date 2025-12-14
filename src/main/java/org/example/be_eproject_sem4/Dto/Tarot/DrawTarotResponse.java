package org.example.be_eproject_sem4.Dto.Tarot;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawTarotResponse {

    private List<DrawnCard> cards;      // 3 lá rút được

    private String aiInterpretation;    // Câu trả lời tổng hợp từ AI
}