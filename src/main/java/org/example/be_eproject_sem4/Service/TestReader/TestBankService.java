package org.example.be_eproject_sem4.Service.TestReader;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.TestReader.TestBankDTO;
import org.example.be_eproject_sem4.Entity.TestBank;
import org.example.be_eproject_sem4.Mapper.TestMapper;
import org.example.be_eproject_sem4.Repository.TestBankRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestBankService {
    private final TestBankRepository repository;
    private final TestMapper mapper; // Giả sử ông đã thêm mapping 2 chiều vào Mapper

    public List<TestBank> getAllQuestions() {
        return repository.findAll();
    }

    public TestBank createQuestion(TestBankDTO dto) {
        TestBank entity = new TestBank();
        entity.setQuestion(dto.getQuestion());
        entity.setOptions(dto.getOptions());
        entity.setCorrectAnswer(dto.getCorrectAnswer());
        entity.setCategory(dto.getCategory());
        return repository.save(entity);
    }

    public TestBank updateQuestion(Long id, TestBankDTO dto) {
        TestBank entity = repository.findById(id).orElseThrow();
        entity.setQuestion(dto.getQuestion());
        entity.setOptions(dto.getOptions());
        entity.setCorrectAnswer(dto.getCorrectAnswer());
        entity.setCategory(dto.getCategory());
        return repository.save(entity);
    }

    public void deleteQuestion(Long id) {
        repository.deleteById(id);
    }
}
