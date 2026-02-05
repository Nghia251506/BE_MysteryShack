package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.ReadingDispute.DisputeResponse;
import org.example.be_eproject_sem4.Entity.ReadingDispute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DisputeMapper {

    // Target (bên trái) là field trong DisputeResponse
    // Source (bên phải) là đường dẫn trong ReadingDispute Entity
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullName")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "readerId", source = "reader.id")
    @Mapping(target = "readerName", source = "reader.fullName")
    @Mapping(target = "amount", source = "session.amount")
    @Mapping(target = "topicName", source = "session.question.topic.name")
    @Mapping(target = "question", source = "session.question.questionText")
    DisputeResponse toResponse(ReadingDispute dispute);
}