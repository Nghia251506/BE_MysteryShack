package org.example.be_eproject_sem4.Service.Rating;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.*;
import org.example.be_eproject_sem4.Dto.Rate.CreateRatingDTO;
import org.example.be_eproject_sem4.Dto.Rate.RatingResponseDTO;
import org.example.be_eproject_sem4.Dto.Rate.ReaderStatsDTO;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Mapper.*;
import org.example.be_eproject_sem4.Repository.*;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.example.be_eproject_sem4.Service.EloService;
import org.example.be_eproject_sem4.Service.FCM.NotificationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ReadingSessionRepository sessionRepository;
    private final ReaderStatsRepository statsRepository;
    private final UserRepository userRepository;
    private final EloService eloService;
    private final RatingMapper ratingMapper;
    private final ReaderStatsMapper statsMapper;
    private final JwtTokenProvider jwtProvider;
    private final NotificationManager notificationManager;

    public Long getCustomerIdFromRequest(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) { // Thay đúng tên cookie của ông
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) throw new RuntimeException("Bạn cần đăng nhập để thực hiện thao tác này!");

        Long userId = jwtProvider.getUserIdFromToken(token);
        if (userId == null) throw new RuntimeException("Phiên đăng nhập hết hạn!");

        return userId;
    }

    // --- 1. LOGIC LÕI: TẠO RATING (4-TRONG-1) ---
    @Transactional
    public RatingResponseDTO createRating(CreateRatingDTO dto, Long customerId) {
        // A. Validate Session
        ReadingSession session = sessionRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên làm việc ID: " + dto.getRequestId()));

        if (session.getIsRated()) {
            throw new RuntimeException("Phiên này đã được đánh giá rồi!");
        }

        // B. Lưu Rating
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với ID: " + customerId));

        Rating rating = ratingMapper.toEntity(dto, session, customer);
        Rating savedRating = ratingRepository.save(rating);

        // C. Update Session
        session.setIsRated(true);
        sessionRepository.save(session);

        // D. Update Stats
        ReaderStats stats = statsRepository.findByReaderId(session.getReader().getId())
                .orElseGet(() -> {
                    ReaderStats newStats = new ReaderStats();
                    newStats.setReader(session.getReader());
                    newStats.setAverageRatingMonth(0.0);
                    newStats.setTotalReviewsMonth(0);
                    return statsRepository.save(newStats);
                });
        stats.updateRating(dto.getRatingValue());
        statsRepository.save(stats);

        // E. Cập nhật Elo
        updateReaderElo(session, dto.getRatingValue(), stats);

        // --- F. BẮN THÔNG BÁO FCM CHO READER ---
        try {
            // Lấy tên khách hàng dựa trên lựa chọn ẩn danh
            String displayName = (dto.getIsAnonymous() != null && dto.getIsAnonymous())
                    ? "Ẩn danh"
                    : customer.getFullName();

            notificationManager.notifyReaderNewRating(
                    session.getReader().getId(),
                    dto.getRatingValue(),
                    dto.getComment(),
                    displayName
            );
        } catch (Exception e) {
            // Log lỗi nhưng không rollback transaction vì đây là phụ trợ (optional)
            System.err.println("Lỗi bắn FCM cho Reader: " + e.getMessage());
        }

        return ratingMapper.toResponseDTO(savedRating);
    }

    // --- 2. API LẤY TỔNG (Trả hết cho FE như ông yêu cầu) ---
    public List<RatingResponseDTO> getAllRatingsByReader(Long readerId) {
        return ratingRepository.findByReaderIdOrderByCreatedAtDesc(readerId)
                .stream()
                .map(ratingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- 3. API BỘ LỌC RIÊNG (Filter API) ---
    public List<RatingResponseDTO> filterRatings(Long readerId, Integer stars, boolean onlyHasComment) {
        List<Rating> result;

        if (stars != null && onlyHasComment) {
            // Lọc cả sao và phải có comment
            result = ratingRepository.findByReaderIdAndRatingValueWithComments(readerId, stars);
        } else if (stars != null) {
            // Chỉ lọc theo số sao
            result = ratingRepository.findByReaderIdAndRatingValueOrderByCreatedAtDesc(readerId, stars);
        } else if (onlyHasComment) {
            // Chỉ lấy những cái có comment
            result = ratingRepository.findByReaderIdWithComments(readerId);
        } else {
            // Không chọn gì thì trả về hết
            result = ratingRepository.findByReaderIdOrderByCreatedAtDesc(readerId);
        }

        return result.stream().map(ratingMapper::toResponseDTO).collect(Collectors.toList());
    }

    // --- 4. LẤY THỐNG KÊ TRUNG BÌNH (ReaderStats) ---
    public ReaderStatsDTO getReaderStats(Long readerId) {
        ReaderStats stats = statsRepository.findByReaderId(readerId)
                .orElseThrow(() -> new RuntimeException("Reader này chưa có thống kê!"));
        return statsMapper.toDTO(stats);
    }

    // --- 5. LẤY DANH SÁCH CHỜ ĐÁNH GIÁ (Dành cho Customer) ---
    public List<ReadingSession> getPendingRatings(Long customerId) {
        // Truyền string "COMPLETED" vào như ông dặn
        return sessionRepository.findPendingRatingsByCustomerId(customerId, "COMPLETED");
    }

    // --- HÀM PHỤ TRỢ TÍNH ELO ---
    private void updateReaderElo(ReadingSession session, Integer stars, ReaderStats stats) {
        User reader = session.getReader();

        EloCalculationRequest eloReq = EloCalculationRequest.builder()
                .currentElo(reader.getEloScore())
                .stars(stars.doubleValue())
                .positiveRate(stats.getAverageRatingMonth() / 5.0)
                // Thay vì lấy field responseTime (có thể null), hãy gọi hàm tính toán:
                .responseTime(session.calculateResponseTime())
                .isCompleted(true)
                .userReputation(session.getCustomer().getReputation())
                .kFactor(32) // Con số 32 này là tiêu chuẩn Elo, tạm để đây là ok
                .build();

        EloCalculationResponse eloRes = eloService.calculateNewElo(eloReq);
        reader.setEloScore(eloRes.getNewElo());
        userRepository.save(reader);
    }
}