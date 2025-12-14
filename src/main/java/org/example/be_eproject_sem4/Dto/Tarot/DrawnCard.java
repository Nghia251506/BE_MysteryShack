package org.example.be_eproject_sem4.Dto.Tarot;
import lombok.*;
import org.example.be_eproject_sem4.Dto.TarotCard.TarotCardResponseDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawnCard {

    private TarotCardResponseDto card;

    private boolean isReversed;

    private String meaning;
}