package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.TarotCard.CreateTarotCardDto;
import org.example.be_eproject_sem4.Dto.TarotCard.TarotCardResponseDto;
import org.example.be_eproject_sem4.Dto.TarotCard.UpdateTarotCardDto;
import org.example.be_eproject_sem4.Entity.TarotCard;
import org.example.be_eproject_sem4.Entity.Arcana;

public class TarotCardMapper {

    public static TarotCard toEntity(CreateTarotCardDto dto) {
        return TarotCard.builder()
                .cardNumber(dto.getCardNumber())
                .nameEn(dto.getNameEn())
                .nameVi(dto.getNameVi())
                .arcana(Arcana.valueOf(dto.getArcana().toUpperCase()))
                .suit(dto.getSuit())
                .imageUrl(dto.getImageUrl())
                .uprightMeaning(dto.getUprightMeaning())
                .reversedMeaning(dto.getReversedMeaning())
                .description(dto.getDescription())
                .isActive(true)
                .build();
    }

    public static void updateEntity(TarotCard entity, UpdateTarotCardDto dto) {
        if (dto.getCardNumber() != null) entity.setCardNumber(dto.getCardNumber());
        if (dto.getNameEn() != null) entity.setNameEn(dto.getNameEn());
        if (dto.getNameVi() != null) entity.setNameVi(dto.getNameVi());
        if (dto.getArcana() != null) entity.setArcana(Arcana.valueOf(dto.getArcana().toUpperCase()));
        if (dto.getSuit() != null) entity.setSuit(dto.getSuit());
        if (dto.getImageUrl() != null) entity.setImageUrl(dto.getImageUrl());
        if (dto.getUprightMeaning() != null) entity.setUprightMeaning(dto.getUprightMeaning());
        if (dto.getReversedMeaning() != null) entity.setReversedMeaning(dto.getReversedMeaning());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) entity.setActive(dto.getIsActive());
    }

    public static TarotCardResponseDto toResponseDto(TarotCard entity) {
        return TarotCardResponseDto.builder()
                .id(entity.getId())
                .cardNumber(entity.getCardNumber())
                .nameEn(entity.getNameEn())
                .nameVi(entity.getNameVi())
                .arcana(entity.getArcana().name())
                .suit(entity.getSuit())
                .imageUrl(entity.getImageUrl())
                .uprightMeaning(entity.getUprightMeaning())
                .reversedMeaning(entity.getReversedMeaning())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }
}