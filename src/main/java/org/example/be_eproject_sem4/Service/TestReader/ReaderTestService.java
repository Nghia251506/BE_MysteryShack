package org.example.be_eproject_sem4.Service.TestReader;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.TestReader.QuestionResponseDTO;
import org.example.be_eproject_sem4.Dto.TestReader.TestResultDTO;
import org.example.be_eproject_sem4.Dto.TestReader.TestSubmitDTO;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Mapper.TestMapper;
import org.example.be_eproject_sem4.Repository.ReaderApplicationRepository;
import org.example.be_eproject_sem4.Repository.ReaderTestAttemptRepository;
import org.example.be_eproject_sem4.Repository.TestBankRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReaderTestService {

    private final TestBankRepository testBankRepository;
    private final ReaderTestAttemptRepository attemptRepository;
    private final ReaderApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final TestMapper testMapper;

    // 1. Lấy đề thi đã được xào nấu
    public List<QuestionResponseDTO> getTestForReader() {
        List<TestBank> questions = testBankRepository.findAll();

        // Trộn thứ tự câu hỏi
        Collections.shuffle(questions);

        return questions.stream().map(q -> {
            QuestionResponseDTO dto = testMapper.toResponseDto(q);
            // Trộn thứ tự các options trong câu hỏi
            List<String> shuffledOptions = new ArrayList<>(dto.getOptions());
            Collections.shuffle(shuffledOptions);
            dto.setOptions(shuffledOptions);
            return dto;
        }).collect(Collectors.toList());
    }

    // 2. Chấm điểm bài thi
    @Transactional
    public TestResultDTO submitTest(TestSubmitDTO submitDTO) {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

        int correctCount = 0;
        List<TestSubmitDTO.AnswerDTO> answers = submitDTO.getAnswers();

        for (TestSubmitDTO.AnswerDTO answer : answers) {
            // Tìm câu hỏi, nếu không thấy thì bỏ qua (hoặc ném lỗi)
            TestBank question = testBankRepository.findById(answer.getQuestionId())
                    .orElse(null);

            if (question != null && question.getCorrectAnswer().equals(answer.getSelectedOption())) {
                correctCount++;
            }
        }

        int total = answers.size();
        double score = (double) correctCount / total * 100;
        String status = score >= 80.0 ? "PASSED" : "FAILED";

        // Lưu kết quả thi
        ReaderTestAttempt attempt = new ReaderTestAttempt();
        attempt.setReader(currentReader);
        attempt.setScore(score);
        attempt.setCorrectCount(correctCount);
        attempt.setTotalQuestions(total);
        attempt.setStatus(status);
        attemptRepository.save(attempt);

        // Nếu ĐỖ, tự động tạo đơn đăng ký cho Admin
        if ("PASSED".equals(status)) {
            createApplication(currentReader, attempt);
        }

        return new TestResultDTO(score, correctCount, total, status);
    }

    private void createApplication(User user, ReaderTestAttempt attempt) {
        if (!applicationRepository.existsByUserId(user.getId())) {
            ReaderApplication app = new ReaderApplication();
            app.setUser(user);
            app.setTestAttempt(attempt);
            app.setStatus(ApplicationStatus.PENDING);
            applicationRepository.save(app);
        }
    }
}
