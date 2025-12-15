// src/main/java/org/example/be_eproject_sem4/Service/AI/AiInterpretationService.java

package org.example.be_eproject_sem4.Service.AI;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Tarot.DrawnCard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiInterpretationService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    private final RestTemplate restTemplate;

    // ===================== TAROT =====================
    private static final String TAROT_PROMPT_TEMPLATE = """
        Bạn là một thầy bói Tarot chuyên nghiệp, ấm áp và sâu sắc.
        Chủ đề khách hỏi: "%s"
        
        %s3 lá bài rút được:
        1. %s (%s): %s
        2. %s (%s): %s
        3. %s (%s): %s
        
        Hãy giải nghĩa tổng hợp 3 lá này một cách mạch lạc, tích cực, có lời khuyên cụ thể.
        Độ dài khoảng 300-400 từ, viết bằng tiếng Việt, giọng ấm áp, truyền cảm hứng.
        """;

    public String generateInterpretation(String topic, List<DrawnCard> cards, LocalDate birthday) {
        String birthdayInfo = "";
        if (birthday != null) {
            int age = Period.between(birthday, LocalDate.now()).getYears();
            String zodiac = getZodiacSign(birthday);
            birthdayInfo = String.format("Khách sinh ngày %s (tuổi %d, cung %s). ", birthday, age, zodiac);
        }

        String prompt = String.format(TAROT_PROMPT_TEMPLATE,
                topic,
                birthdayInfo,
                cards.get(0).getCard().getNameVi(), cards.get(0).isReversed() ? "ngược" : "xuôi", cards.get(0).getMeaning(),
                cards.get(1).getCard().getNameVi(), cards.get(1).isReversed() ? "ngược" : "xuôi", cards.get(1).getMeaning(),
                cards.get(2).getCard().getNameVi(), cards.get(2).isReversed() ? "ngược" : "xuôi", cards.get(2).getMeaning()
        );

        return callOpenAI(prompt);
    }

    // ===================== ZODIAC =====================
    private static final String ZODIAC_PROMPT_TEMPLATE = """
        Bạn là một nhà chiêm tinh học chuyên nghiệp, ấm áp và truyền cảm hứng.
        Thông tin khách hàng:
        - Tên: %s
        - Giới tính: %s
        - Ngày sinh: %s
        - Cung hoàng đạo: %s
        
        Hãy đưa ra phân tích chi tiết về cung hoàng đạo này theo các phần sau bằng tiếng Việt, giọng ấm áp, tích cực:
        
        ## Tổng quan
        ## Điểm mạnh
        ## Điểm yếu
        ## Tính cách
        ## Gia đình
        ## Tình yêu
        ## Tình dục
        ## Sự nghiệp
        
        Độ dài mỗi phần khoảng 80-120 từ, tổng khoảng 600-800 từ.
        """;

    public String generateZodiacResponse(String name, String gender, LocalDate birthday, String zodiacNameVi) {
        String prompt = String.format(ZODIAC_PROMPT_TEMPLATE,
                name,
                gender,
                birthday,
                zodiacNameVi
        );

        return callOpenAI(prompt);
    }

    // ===================== COMMON =====================
    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.8,
                "max_tokens", 1500
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    entity,
                    Map.class
            );

            Map body = response.getBody();
            if (body != null && response.getStatusCode().is2xxSuccessful()) {
                List<?> choices = (List<?>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
                    return (String) message.get("content");
                }
            }
            return "Lỗi từ AI: " + (body != null ? body.get("error") : "Không có phản hồi");
        } catch (Exception e) {
            return "Lỗi kết nối AI: " + e.getMessage();
        }
    }

    private String getZodiacSign(LocalDate birthday) {
        int day = birthday.getDayOfMonth();
        int month = birthday.getMonthValue();

        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Bạch Dương";
        if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Kim Ngưu";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 21)) return "Song Tử";
        if ((month == 6 && day >= 22) || (month == 7 && day <= 22)) return "Cự Giải";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Sư Tử";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Xử Nữ";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 23)) return "Thiên Bình";
        if ((month == 10 && day >= 24) || (month == 11 && day <= 22)) return "Bọ Cạp";
        if ((month == 11 && day >= 23) || (month == 12 && day <= 21)) return "Nhân Mã";
        if ((month == 12 && day >= 22) || (month == 1 && day <= 20)) return "Ma Kết";
        if ((month == 1 && day >= 21) || (month == 2 && day <= 18)) return "Bảo Bình";
        if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Song Ngư";
        return "Không xác định";
    }
}