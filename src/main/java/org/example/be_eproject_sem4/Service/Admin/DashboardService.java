package org.example.be_eproject_sem4.Service.Admin;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.be_eproject_sem4.Dto.Admin.DashboardStatsDTO;
import org.example.be_eproject_sem4.Dto.Admin.SessionDashboardDTO;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ReadingSessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public List<SessionDashboardDTO> getRecentSessions() {
        return sessionRepo.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .toList();
    }

    // Gửi thông báo Real-time khi khách hàng vừa bấm đặt phiên
    public void broadcastNewSession(ReadingSession session) {
        // 1. Chuyển đổi Entity sang DTO cho bảng
        SessionDashboardDTO dto = mapToDto(session);

        // 2. Bắn xuống channel /topic/admin/sessions
        // Client (React) sẽ lắng nghe channel này để chèn thêm dòng vào bảng
        messagingTemplate.convertAndSend("/topic/admin/sessions", dto);

        // 3. (Tùy chọn) Bắn thêm một tin nhắn tới channel /topic/admin/stats
        // để các ô số (Stats Cards) nhảy theo
        DashboardStatsDTO stats = getStats();
        messagingTemplate.convertAndSend("/topic/admin/stats", stats);
    }

    public DashboardStatsDTO getStats() {
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        long totalToday = sessionRepo.countByCreatedAtAfter(startOfDay);
        long active = sessionRepo.countByStatus("IN_PROGRESS");
        // Giả sử ông đếm User có role READER và isActive = true
        long onlineReaders = userRepo.countByRoleAndIsActive(User.Role.READER, true);
        long newCustomers = userRepo.countByRoleAndCreatedAtAfter(User.Role.CUSTOMER, LocalDateTime.now().minusDays(1));

        return new DashboardStatsDTO(totalToday, active, onlineReaders, newCustomers, newCustomers, newCustomers,
                newCustomers, newCustomers);
    }

    private SessionDashboardDTO mapToDto(ReadingSession s) {
        return SessionDashboardDTO.builder()
                .id("SS" + s.getId())
                .cName(s.getCustomer() != null ? s.getCustomer().getFullName() : "Khách ẩn danh")
                .cId("C" + (s.getCustomer() != null ? s.getCustomer().getId() : "0"))
                .rName(s.getReader() != null ? s.getReader().getFullName() : "Đang chờ...")
                .rId("R" + (s.getReader() != null ? s.getReader().getId() : "0"))
                .topic(s.getQuestion() != null ? s.getQuestion().getTopic().getName() : "General")
                .question(s.getQuestion() != null ? s.getQuestion().getQuestionText() : "N/A")
                .status(s.getStatus())
                .time(s.getCreatedAt().toString()) // Frontend sẽ convert sang "X phút trước"
                .build();
    }

    public List<Map<String, Object>> getHourlyChartData() {
        // 1. Lấy data thô từ Repo: [[8, 5], [10, 12]] (Giờ 8 có 5 phiên, giờ 10 có 12
        // phiên)
        List<Object[]> rawData = sessionRepo.getHourlyStatsNative();

        // 2. Tạo một Map để tra cứu cho nhanh
        Map<Integer, Long> statsMap = new HashMap<>();
        for (Object[] row : rawData) {
            statsMap.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }

        // 3. Tạo mảng 24 phần tử để trả về cho Recharts
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Map<String, Object> dataPoint = new HashMap<>();
            String timeLabel = String.format("%02d:00", i);

            dataPoint.put("time", timeLabel);
            // Nếu giờ i có data thì lấy, không thì để là 0
            dataPoint.put("sessions", statsMap.getOrDefault(i, 0L));

            chartData.add(dataPoint);
        }

        return chartData;
    }

    public List<User> getTopReaders() {
        // Lấy Top 3 Reader có Elo cao nhất và đang hoạt động
        return userRepo.findTop3ByRoleAndIsActiveOrderByEloScoreDesc(User.Role.READER, true);
    }

    public List<SessionDashboardDTO> searchSessions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getRecentSessions(); // Nếu không gõ gì thì trả về 10 cái mới nhất
        }

        return sessionRepo.findByKeyword(keyword).stream()
                .map(this::mapToDto)
                .toList();
    }
}
