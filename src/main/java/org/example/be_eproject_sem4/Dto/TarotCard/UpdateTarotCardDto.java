package org.example.be_eproject_sem4.Dto.TarotCard;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTarotCardDto {

    private Integer cardNumber;
    private String nameEn;
    private String nameVi;
    private String arcana;
    private String suit;
    private String imageUrl;
    private String uprightMeaning;
    private String reversedMeaning;
    private String description;
    private Set<String> keywords;
    private Boolean isActive;
}