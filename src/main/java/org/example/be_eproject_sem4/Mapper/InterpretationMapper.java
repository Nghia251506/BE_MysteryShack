package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.InterpretationResponseDto;
import org.example.be_eproject_sem4.Entity.InterpretationForm;
import org.example.be_eproject_sem4.Entity.InterpretationStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class InterpretationMapper {

    /**
     * Map từ Entity sang DTO
     * sessionId được lấy từ id của requestId (ReadingSession)
     * qrPayment trong Entity và DTO trùng tên nên MapStruct tự động map
     */
    @Mapping(target = "sessionId", source = "requestId.id")
    public abstract InterpretationResponseDto toDto(InterpretationForm entity);

    /**
     * Logic bảo mật: Chỉ cho phép xem lá bài 2, 3 và lời khuyên khi đã thanh toán.
     * Lá bài 1 (interpretation1) và QR Payment luôn hiển thị.
     */
    @AfterMapping
    protected void filterContentForCustomer(@MappingTarget InterpretationResponseDto dto, InterpretationForm entity) {
        
        // Kiểm tra xem trạng thái có phải là PAID hoặc COMPLETED không
        boolean isAccessible = entity.getStatus() == InterpretationStatus.PAID || 
                               entity.getStatus() == InterpretationStatus.COMPLETED;
        
        if (!isAccessible) {
            // 1. Giữ nguyên dto.getInterpretation1() (Khách được xem lá 1)
            
            // 2. Ghi đè thông báo khóa cho các trường còn lại
            dto.setInterpretation2("Nội dung đã bị khóa. Vui lòng thanh toán để xem tiếp.");
            dto.setInterpretation3("Nội dung đã bị khóa. Vui lòng thanh toán để xem tiếp.");
            dto.setAdvice("Lời khuyên tổng kết sẽ hiển thị sau khi bạn thanh toán.");
            
            // 3. QR Payment vẫn giữ nguyên để khách có thông tin thanh toán
        }
        // Nếu isAccessible = true, MapStruct đã tự động đổ dữ liệu thật từ Entity vào DTO rồi.
    }
}