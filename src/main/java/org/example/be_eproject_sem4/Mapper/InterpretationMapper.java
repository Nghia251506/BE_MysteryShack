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
     * 1. sessionId lấy từ requestId.id
     * 2. questionContent lấy từ requestId.question.content
     * 3. selectedCards lấy từ requestId.selectedCards
     * 4. ReaderInfo được map thủ công trong AfterMapping để linh hoạt
     */
    @Mapping(target = "sessionId", source = "requestId.id")
    @Mapping(target = "questionContent", source = "requestId.question.questionText")
    @Mapping(target = "selectedCards", source = "requestId.selectedCards")
    @Mapping(target = "reader", ignore = true) // Sẽ map trong AfterMapping
    @Mapping(target = "amount", source = "requestId.amount")
    @Mapping(target = "reader.profilePicture", source = "avatar")
    public abstract InterpretationResponseDto toDto(InterpretationForm entity);

    @AfterMapping
    protected void mapAdditionalInfo(@MappingTarget InterpretationResponseDto dto, InterpretationForm entity) {
        // 1. Map thông tin Reader từ ReadingSession
        if (entity.getRequestId() != null && entity.getRequestId().getReader() != null) {
            InterpretationResponseDto.ReaderInfo readerDto = new InterpretationResponseDto.ReaderInfo();
            readerDto.setId(entity.getRequestId().getReader().getId());
            readerDto.setFullName(entity.getRequestId().getReader().getFullName());
            // readerDto.setAvatar(entity.getRequestId().getReader().getAvatar()); // Nếu có trường avatar
            dto.setReader(readerDto);
        }

        // 2. Logic bảo mật (Cũ của bạn)
        boolean isAccessible = entity.getStatus() == InterpretationStatus.PAID || 
                               entity.getStatus() == InterpretationStatus.COMPLETED;
        
        if (!isAccessible) {
            dto.setInterpretation2("Nội dung đã bị khóa. Vui lòng thanh toán để xem tiếp.");
            dto.setInterpretation3("Nội dung đã bị khóa. Vui lòng thanh toán để xem tiếp.");
            dto.setAdvice("Lời khuyên tổng kết sẽ hiển thị sau khi bạn thanh toán.");
        }
    }
}