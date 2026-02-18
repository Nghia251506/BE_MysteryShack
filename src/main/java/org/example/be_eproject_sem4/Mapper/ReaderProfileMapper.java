package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.ReaderProfile.*;
import org.example.be_eproject_sem4.Entity.Rating;
import org.example.be_eproject_sem4.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReaderProfileMapper {

    @Mapping(target = "id", source = "reader.id")
    @Mapping(target = "fullName", source = "reader.fullName")
    @Mapping(target = "avatarUrl", source = "reader.profilePicture")
    @Mapping(target = "status", expression = "java(reader.getIsBusy() ? \"BUSY\" : \"ONLINE\")")
    @Mapping(target = "experienceYears", source = "reader.createdAt", qualifiedByName = "calculateExp")
    @Mapping(target = "recentReviews", source = "ratings")
    // Các trường averageRating, totalReviews, bookingPrice nên set thủ công từ Service vì cần tính toán từ DB
    ReaderProfileDTO toProfileDTO(User reader, List<Rating> ratings);

    @Mapping(target = "customerName", expression = "java(rating.getIsAnonymous() ? \"Người dùng ẩn danh\" : rating.getCustomer().getFullName())")
    @Mapping(target = "customerAvatar", source = "customer.profilePicture")
    @Mapping(target = "ratingValue", source = "ratingValue")
    @Mapping(target = "comment", source = "replyComment") // Nếu comment của khách nằm ở request, ông map lại path nhé
    @Mapping(target = "createdAt", source = "createdAt")
    ReaderReviewDTO toReviewDTO(Rating rating);

    @Named("calculateExp")
    default Integer calculateExp(LocalDateTime createdAt) {
        if (createdAt == null) return 0;
        int years = Year.now().getValue() - createdAt.getYear();
        return Math.max(years, 1); // Trả về ít nhất 1 năm kinh nghiệm nếu vừa tạo
    }
}