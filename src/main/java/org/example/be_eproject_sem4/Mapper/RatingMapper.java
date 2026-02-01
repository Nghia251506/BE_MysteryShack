package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.Rate.CreateRatingDTO;
import org.example.be_eproject_sem4.Dto.Rate.RatingResponseDTO;
import org.example.be_eproject_sem4.Entity.Rating;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    // --- 1. TO ENTITY ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", source = "session")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "reader", source = "session.reader")
    @Mapping(target = "ratingValue", source = "dto.ratingValue")
    @Mapping(target = "replyComment", source = "dto.comment")
    @Mapping(target = "isAnonymous", source = "dto.isAnonymous")
    @Mapping(target = "createdAt", ignore = true)
    Rating toEntity(CreateRatingDTO dto, ReadingSession session, User customer);

    // --- 2. TO RESPONSE DTO ---
    // Ở đây mình map thẳng 'entity' vào các hàm xử lý tên/avatar
    @Mapping(target = "customerName", source = "entity", qualifiedByName = "mapCustomerName")
    @Mapping(target = "customerAvatar", source = "entity", qualifiedByName = "mapCustomerAvatar")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatInstant")
    RatingResponseDTO toResponseDTO(Rating entity);

    @Named("mapCustomerName")
    default String mapCustomerName(Rating entity) {
        if (entity == null || entity.getCustomer() == null) return "Người dùng";
        if (Boolean.TRUE.equals(entity.getIsAnonymous())) return "Người dùng ẩn danh";
        return entity.getCustomer().getFullName();
    }

    // --- ĐÂY LÀ HÀM ÔNG ĐANG THIẾU DẪN ĐẾN LỖI TRONG ẢNH ---
    @Named("mapCustomerAvatar")
    default String mapCustomerAvatar(Rating entity) {
        if (entity == null || entity.getCustomer() == null) return null;
        if (Boolean.TRUE.equals(entity.getIsAnonymous())) return null; // Ẩn danh thì không hiện avatar
        return entity.getCustomer().getProfilePicture();
    }

    @Named("formatInstant")
    default String formatInstant(Instant instant) {
        if (instant == null) return null;
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
}