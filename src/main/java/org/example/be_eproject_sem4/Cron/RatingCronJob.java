package org.example.be_eproject_sem4.Cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.be_eproject_sem4.Entity.RatingMonthlyHistory;
import org.example.be_eproject_sem4.Entity.ReaderStats;
import org.example.be_eproject_sem4.Repository.RatingMonthlyHistoryRepository;
import org.example.be_eproject_sem4.Repository.ReaderStatsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingCronJob {

    private final ReaderStatsRepository statsRepository;
    private final RatingMonthlyHistoryRepository historyRepository;

    // Chạy vào lúc 23:59:59 ngày cuối cùng của mỗi tháng
    // Giây - Phút - Giờ - Ngày - Tháng - Thứ
    @Scheduled(cron = "59 59 23 L * ?")
    @Transactional
    public void archiveMonthlyStats() {
        log.info("Bắt đầu chốt sổ dữ liệu Rating hàng tháng...");

        // 1. Lấy tất cả Reader có hoạt động trong tháng
        List<ReaderStats> allStats = statsRepository.findAll();
        LocalDate now = LocalDate.now();

        for (ReaderStats stats : allStats) {
            if (stats.getTotalReviewsMonth() > 0) {
                // 2. Lưu vào bảng History để làm biểu đồ sau này
                RatingMonthlyHistory history = new RatingMonthlyHistory();
                history.setReader(stats.getReader());
                history.setFinalAvgRating(stats.getAverageRatingMonth());
                history.setTotalReviews(stats.getTotalReviewsMonth());
                history.setMonth(now.getMonthValue());
                history.setYear(now.getYear());

                historyRepository.save(history);

                // 3. Reset chỉ số tháng của Reader về 0 để sang tháng mới
                stats.setAverageRatingMonth(0.0);
                stats.setTotalReviewsMonth(0);
                // Lưu ý: totalReviews (tổng đời) thì giữ nguyên, không được reset
                statsRepository.save(stats);
            }
        }

        log.info("Đã chốt sổ thành công cho {} Reader.", allStats.size());
    }
}