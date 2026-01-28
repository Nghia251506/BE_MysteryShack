package org.example.be_eproject_sem4.Service;

import org.example.be_eproject_sem4.Dto.EloCalculationRequest;
import org.example.be_eproject_sem4.Dto.EloCalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class EloService {

    public EloCalculationResponse calculateNewElo(EloCalculationRequest request) {
        // 1. Tính Chỉ số Phản hồi (P)
        // P = 1.0 nếu < 15p, 0.5 nếu 15-30p, 0.0 nếu > 30p
        double p = 0.0;
        if (request.getResponseTime() < 15) p = 1.0;
        else if (request.getResponseTime() <= 30) p = 0.5;

        // 2. Tính Chỉ số Hoàn thành (C)
        double c = request.isCompleted() ? 1.0 : 0.0;

        // 3. Quy đổi Hài lòng (H) THEO CÔNG THỨC MỚI
        // S: Số sao (1-5)
        // R: Tỷ lệ phản hồi tích cực (Dạng số thập phân từ 0.0 đến 1.0)
        // Công thức: H = (S/5 * 0.7) + (R * 0.3)
        double s = request.getStars();
        double r = request.getPositiveRate(); // Giả định DTO của bạn có thêm trường này
        double h = ((s / 5.0) * 0.7) + (r * 0.3);

        // 4. Tính Điểm thực tế (A)
        // Trọng số: P(30%), C(30%), H(40%)
        double a = (0.3 * p) + (0.3 * c) + (0.4 * h);

        // 5. Tính Điểm kỳ vọng (E)
        // Dựa trên sự chênh lệch giữa danh tiếng người dùng và Elo hiện tại của Reader
        double e = 1 / (1 + Math.pow(10, (request.getUserReputation() - request.getCurrentElo()) / 400.0));

        // 6. Tính Elo mới
        double newElo = request.getCurrentElo() + request.getKFactor() * (a - e);

        // Chuẩn bị kết quả trả về
        EloCalculationResponse response = new EloCalculationResponse();
        response.setActualScore(Math.round(a * 100.0) / 100.0);
        response.setExpectedScore(Math.round(e * 100.0) / 100.0);
        response.setNewElo(Math.round(newElo * 100.0) / 100.0);
        response.setEvaluation(determineEvaluation(a));

        return response;
    }

    private String determineEvaluation(double a) {
        if (a >= 0.9) return "Tăng Elo tối đa. Reader đang làm rất tốt!";
        if (a >= 0.7) return "Tăng Elo ổn định.";
        if (a >= 0.5) return "Tăng nhẹ, cần cải thiện tốc độ hoặc phản hồi.";
        return "Giảm Elo mạnh, ảnh hưởng trực tiếp đến việc nhận đơn.";
    }
}