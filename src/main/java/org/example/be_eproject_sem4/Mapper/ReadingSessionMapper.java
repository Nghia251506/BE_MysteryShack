package org.example.be_eproject_sem4.Mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface ReadingSessionMapper {

    // 1. Map questionText thay vì title
    // 2. Sử dụng hàm chuyển đổi selectedCards thủ công qua qualifiedByName
    @Mapping(target = "questionName", source = "question.questionText")
    // Ánh xạ thông tin từ đối tượng customer bên trong ReadingSession
    @Mapping(target = "fullName", source = "customer.fullName")
    @Mapping(target = "birthDate", source = "customer.birthDate")
    @Mapping(target = "selectedCards", source = "selectedCards")
    ReadingSessionSimpleDto toSimpleDto(ReadingSession entity);

    List<ReadingSessionSimpleDto> toSimpleDtoList(List<ReadingSession> entities);

    // Hàm bổ trợ để chuyển đổi chuỗi JSON String sang List<Long>
    @Named("jsonToList")
    default List<Long> jsonToList(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Named("selectedCardsToLongList")
    default List<Long> selectedCardsToLongList(Object selectedCards) {
        if (selectedCards == null) return new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<?> cardList = mapper.convertValue(selectedCards, new TypeReference<List<?>>() {});
            List<Long> result = new ArrayList<>();
            for (Object card : cardList) {
                if (card instanceof Number) {
                    result.add(((Number) card).longValue());
                }
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}