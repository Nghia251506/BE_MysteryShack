package org.example.be_eproject_sem4.Service;

import org.example.be_eproject_sem4.Dto.EloCalculationRequest;
import org.example.be_eproject_sem4.Dto.EloCalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class EloService {

    // Thiết lập ngưỡng để tránh lạm phát điểm hoặc âm điểm
    private static final double MAX_ELO = 500.0;
    private static final double MIN_ELO = 100.0;

    public EloCalculationResponse calculateNewElo(EloCalculationRequest request) {
        // 1. Tính Chỉ số Phản hồi (P)
        // P = 1.0 nếu < 15p, 0.5 nếu 15-30p, 0.0 nếu > 30p
        double p = 0.0;
        if (request.getResponseTime() < 15) p = 1.0;
        else if (request.getResponseTime() <= 30) p = 0.5;

        // 2. Tính Chỉ số Hoàn thành (C)
        double c = request.isCompleted() ? 1.0 : 0.0;

        // 3. Quy đổi Hài lòng (H)
        double s = request.getStars();
        double r = request.getPositiveRate();
        double h ;
        if (s == 0) {
            // Nếu khách chưa đánh giá, cho Reader hưởng mức Hài lòng mặc định (ví dụ 0.8 ~ 4 sao)
            // Để họ vẫn được tăng Elo nhờ tốc độ (P) và hoàn thành đơn (C)
            h = 0.8;
        } else {
            h = ((s / 5.0) * 0.7) + (r * 0.3);
        }

        // 4. Tính Điểm thực tế (A) - Trọng số: P(30%), C(30%), H(40%)
        double a = (0.3 * p) + (0.3 * c) + (0.4 * h);

        // 5. Tính Điểm kỳ vọng (E)
        // Công thức chuẩn Elo: E = 1 / (1 + 10^((Reputation - CurrentElo) / 400))
        double e = 1 / (1 + Math.pow(10, (request.getUserReputation() - request.getCurrentElo()) / 400.0));

        // 6. Tính Elo mới tạm thời
        double calculatedElo = request.getCurrentElo() + request.getKFactor() * (a - e);

        // 7. ÁP DỤNG "PHANH" - Chặn ngưỡng Max và Min
        double finalElo = Math.min(MAX_ELO, Math.max(MIN_ELO, calculatedElo));

        // Chuẩn bị kết quả trả về
        EloCalculationResponse response = new EloCalculationResponse();
        response.setActualScore(Math.round(a * 100.0) / 100.0);
        response.setExpectedScore(Math.round(e * 100.0) / 100.0);

        // Làm tròn Elo về 2 chữ số thập phân cho đẹp DB
        response.setNewElo(Math.round(finalElo * 100.0) / 100.0);
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