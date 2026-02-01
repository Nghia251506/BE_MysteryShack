package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.Rate.ReaderStatsDTO;
import org.example.be_eproject_sem4.Entity.ReaderStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReaderStatsMapper {

    // Làm tròn số sao ngay tại Mapping luôn
    @Mapping(target = "averageRatingMonth", expression = "java(Math.round(entity.getAverageRatingMonth() * 10.0) / 10.0)")
    ReaderStatsDTO toDTO(ReaderStats entity);
}