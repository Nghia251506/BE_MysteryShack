package org.example.be_eproject_sem4.Dto.Zodiac;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZodiacAskRequestDto {

    @NotBlank(message = "Tên khách không được để trống")
    private String name;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate birthday;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender; // "Nam", "Nữ", "Khác"
}
