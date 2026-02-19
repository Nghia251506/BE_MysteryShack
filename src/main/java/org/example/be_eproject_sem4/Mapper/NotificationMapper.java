package org.example.be_eproject_sem4.Mapper;
import org.example.be_eproject_sem4.Dto.Notification.*;
import org.example.be_eproject_sem4.Entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring") // Để Spring quản lý như một Bean (@Autowired được)
public interface NotificationMapper {

    // 1. Từ Request (FE) sang Entity để lưu DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SENT") // Mặc định là SENT khi gửi từ FE
    @Mapping(target = "readCount", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    Notification toEntity(NotificationRequest request);

    // 2. Từ Entity sang Response (để hiện ở bảng History)
    @Mapping(target = "date", source = "createdAt", qualifiedByName = "formatDate")
    @Mapping(target = "recipient", source = "entity", qualifiedByName = "mapRecipient")
    NotificationResponse toResponse(Notification entity);

    // --- Logic "nêm nếm" thêm ---

    @Named("formatDate")
    default String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Named("mapRecipient")
    default String mapRecipient(Notification entity) {
        if ("Specific".equalsIgnoreCase(entity.getRecipientGroup())) {
            return entity.getSpecificId();
        }
        // Map nhãn tiếng Việt cho đẹp giống Mock Data của ông
        return switch (entity.getRecipientGroup()) {
            case "All" -> "Tất cả người dùng";
            case "AllReaders" -> "Tất cả Reader";
            case "AllCustomers" -> "Tất cả Khách hàng";
            default -> entity.getRecipientGroup();
        };
    }
}
