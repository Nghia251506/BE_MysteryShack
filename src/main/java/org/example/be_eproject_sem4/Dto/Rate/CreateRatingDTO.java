package org.example.be_eproject_sem4.Dto.Rate;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRatingDTO {
    @NotNull(message = "Request ID không được để trống")
    private Long requestId; // ID của ReadingSession

    @Min(value = 1, message = "Rating thấp nhất là 1 sao")
    @Max(value = 5, message = "Rating cao nhất là 5 sao")
    private Integer ratingValue;

    private String comment;

    private Boolean isAnonymous = false;
}
