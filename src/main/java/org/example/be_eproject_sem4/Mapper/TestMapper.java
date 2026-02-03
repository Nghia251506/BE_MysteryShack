package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.TestReader.QuestionResponseDTO;
import org.example.be_eproject_sem4.Dto.TestReader.TestResultDTO;
import org.example.be_eproject_sem4.Entity.ReaderTestAttempt;
import org.example.be_eproject_sem4.Entity.TestBank;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestMapper {
    // Chuyển từ Entity sang DTO cho đề thi
    QuestionResponseDTO toResponseDto(TestBank entity);

    // Chuyển danh sách
    List<QuestionResponseDTO> toResponseDtoList(List<TestBank> entities);

    // Chuyển kết quả thi sang DTO hiển thị
    TestResultDTO toResultDto(ReaderTestAttempt entity);
}
