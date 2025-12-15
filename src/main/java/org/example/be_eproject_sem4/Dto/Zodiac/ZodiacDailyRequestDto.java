package org.example.be_eproject_sem4.Dto.Zodiac;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZodiacDailyRequestDto {

    @NotBlank(message = "Tên tiếng Việt không được để trống")
    private String nameVi;

    @NotBlank(message = "Tên tiếng Anh không được để trống")
    private String nameEn;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    private String description;

    private String imageUrl;
}
