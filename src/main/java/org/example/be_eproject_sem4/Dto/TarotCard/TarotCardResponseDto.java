package org.example.be_eproject_sem4.Dto.TarotCard;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarotCardResponseDto {
    private Long id;
    private Integer cardNumber;
    private String nameEn;
    private String nameVi;
    private String arcana;  // MAJOR/MINOR
    private String suit;
    private String imageUrl;
    private String uprightMeaning;
    private String reversedMeaning;
    private String description;
    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}