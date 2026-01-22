package org.example.be_eproject_sem4.Converter;

import java.util.Collections;
import java.util.List;

import org.example.be_eproject_sem4.Dto.SelectedCardDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;

@Converter
public class SelectedCardsConverter implements AttributeConverter<List<SelectedCardDto>, String> {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<SelectedCardDto> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<SelectedCardDto> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<SelectedCardDto>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}
