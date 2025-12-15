package org.example.be_eproject_sem4.Dto.Zodiac;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZodiacSignResponseDto {

    private Long id;

    private String nameVi;

    private String nameEn;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private String imageUrl;

    private boolean active;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}