package org.example.be_eproject_sem4.Service;

import org.example.be_eproject_sem4.Dto.EloCalculationRequest;
import org.example.be_eproject_sem4.Dto.EloCalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class EloService {

    public EloCalculationResponse calculateNewElo(EloCalculationRequest request) {
        // 1. Tính Chỉ số Phản hồi (P) [cite: 31, 32, 33]
        double p = 0.0;
        if (request.getResponseTime() < 15) p = 1.0;
        else if (request.getResponseTime() <= 30) p = 0.5;

        // 2. Tính Chỉ số Hoàn thành (C)
        double c = request.isCompleted() ? 1.0 : 0.0;

        // 3. Quy đổi Hài lòng (H) từ số sao
        double h = switch (request.getStars()) {
            case 5 -> 1.0;
            case 4 -> 0.8;
            case 3 -> 0.5;
            case 2 -> 0.2;
            default -> 0.0;
        };

        // 4. Tính Điểm thực tế (A)
        double a = (0.3 * p) + (0.3 * c) + (0.4 * h);

        // 5. Tính Điểm kỳ vọng (E)
        double e = 1 / (1 + Math.pow(10, (request.getUserReputation() - request.getCurrentElo()) / 400));

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
        if (a >= 1.0) return "Tăng Elo tối đa.";
        if (a >= 0.8) return "Tăng Elo nhưng bị hạn chế do tốc độ hoặc chất lượng.";
        if (a < 0.5) return "Giảm Elo mạnh, ảnh hưởng trực tiếp đến việc nhận đơn.";
        return "Tăng Elo nhẹ, cần cải thiện.";
    }
}
