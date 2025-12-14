package org.example.be_eproject_sem4.Dto.TarotCard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTarotCardDto {

    @NotNull(message = "Số thứ tự không được để trống")
    private Integer cardNumber;

    @NotBlank(message = "Tên tiếng Anh không được để trống")
    private String nameEn;

    private String nameVi;

    @NotNull(message = "Loại Arcana không được để trống")
    private String arcana;  // "MAJOR" hoặc "MINOR"

    private String suit;  // Chỉ dùng khi MINOR

    private String imageUrl;

    private String uprightMeaning;

    private String reversedMeaning;

    private String description;

    private Set<String> keywords;
}