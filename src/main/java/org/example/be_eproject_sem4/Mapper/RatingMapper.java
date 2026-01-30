package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.RatingRequest;
import org.example.be_eproject_sem4.Dto.RatingResponse;
import org.example.be_eproject_sem4.Entity.Rating;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    @Mapping(target = "request", source = "request")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "reader", source = "reader")
    RatingResponse toDto(Rating entity);

    @Mapping(target = "request.id", source = "requestId")
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "reader.id", source = "readerId")
    Rating toRequestDto(RatingRequest entity);
}
