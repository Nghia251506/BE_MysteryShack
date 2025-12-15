package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacSignResponseDto;
import org.example.be_eproject_sem4.Entity.ZodiacDaily;

public class ZodiacSignMapper {

    public static ZodiacSignResponseDto toDto(ZodiacDaily zodiacSign) {
        if (zodiacSign == null) {
            return null;
        }

        ZodiacSignResponseDto dto = new ZodiacSignResponseDto();
        dto.setId(zodiacSign.getId());
        dto.setNameVi(zodiacSign.getNameVi());
        dto.setNameEn(zodiacSign.getNameEn());
        dto.setStartDate(zodiacSign.getStartDate());
        dto.setEndDate(zodiacSign.getEndDate());
        dto.setDescription(zodiacSign.getDescription());
        dto.setImageUrl(zodiacSign.getImageUrl());
        dto.setActive(zodiacSign.isActive());
        dto.setCreatedAt(zodiacSign.getCreatedAt());
        dto.setUpdatedAt(zodiacSign.getUpdatedAt());

        return dto;
    }
}