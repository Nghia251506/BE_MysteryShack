package org.example.be_eproject_sem4.Service.TestReader;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.TestReader.ApprovalRequestDTO;
import org.example.be_eproject_sem4.Entity.ApplicationStatus;
import org.example.be_eproject_sem4.Entity.ReaderApplication;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.ReaderApplicationRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminApprovalService {

    private final ReaderApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void approveReader(ApprovalRequestDTO request) {
        ReaderApplication app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ đăng ký"));

        // 1. Cập nhật trạng thái đơn đăng ký
        app.setStatus(ApplicationStatus.APPROVED);
        app.setAdminNote(request.getAdminNote());
        applicationRepository.save(app);

        // 2. Kích hoạt User (Mở khóa để Reader bắt đầu làm việc)
        User user = app.getUser();
        user.setIsBlocked(false);   // Hết bị chặn
        user.setActive(true);     // Trạng thái hoạt động
        user.setVerified(true);   // Đã xác thực hồ sơ
        userRepository.save(user);
    }

    @Transactional
    public void rejectReader(ApprovalRequestDTO request) {
        ReaderApplication app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ đăng ký"));

        // 1. Cập nhật trạng thái đơn là bị từ chối
        app.setStatus(ApplicationStatus.REJECTED);
        app.setAdminNote(request.getAdminNote());
        applicationRepository.save(app);

        // 2. Vẫn giữ isBlocked = true để họ không vào sàn được
        // Có thể gửi email thông báo lý do bị từ chối ở đây
    }

    // Lấy danh sách hồ sơ đang chờ duyệt
    public List<ReaderApplication> getPendingApplications() {
        return applicationRepository.findAllByStatus(ApplicationStatus.PENDING);
    }
}