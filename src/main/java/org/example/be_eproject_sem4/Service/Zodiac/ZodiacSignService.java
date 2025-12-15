package org.example.be_eproject_sem4.Service.Zodiac;

import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacAskRequestDto;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacDailyRequestDto;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacSignResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ZodiacSignService {
    List<ZodiacSignResponseDto> getAllZodiacSigns();
    List<ZodiacSignResponseDto> getActiveZodiacSigns();
    ZodiacSignResponseDto getZodiacSignById(Long id);
    ZodiacSignResponseDto createZodiacSign(ZodiacDailyRequestDto requestDto);
    ZodiacSignResponseDto updateZodiacSign(Long id, ZodiacDailyRequestDto requestDto);
    void deleteZodiacSign(Long id);
    ZodiacSignResponseDto getZodiacSignByBirthDate(LocalDate birthDate);
    ZodiacSignResponseDto getZodiacSignByNameVi(String nameVi);
    ZodiacSignResponseDto getZodiacSignByNameEn(String nameEn);
    String getZodiacAIResponse(ZodiacAskRequestDto requestDto);
}
