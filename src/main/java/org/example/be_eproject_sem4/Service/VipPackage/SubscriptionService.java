package org.example.be_eproject_sem4.Service.VipPackage;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.VipPackage.SubscriptionAdminResponse;
import org.example.be_eproject_sem4.Entity.Subscription;
import org.example.be_eproject_sem4.Entity.SubscriptionStatus;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.VipPackage;
import org.example.be_eproject_sem4.Repository.SubscriptionRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Repository.VipPackageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final VipPackageRepository vipPackageRepository;
    private final UserRepository userRepository;

    @Transactional
    public void activateSubscription(Integer packageId) {
        // 1. Lấy Reader đang login hiện tại - Y hệt cách ông làm
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentReader = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader để kích hoạt gói"));

        // 2. Tìm thông tin gói VIP
        VipPackage pkg = vipPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Gói VIP không tồn tại"));

        // 3. Logic xử lý gói cũ: Tìm gói còn hạn để hủy (hoặc cộng dồn)
        subscriptionRepository.findValidSubscription(currentReader.getId())
                .ifPresent(oldSub -> {
                    oldSub.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(oldSub);
                });

        // 4. Tạo Subscription mới
        Subscription sub = new Subscription();
        sub.setReader(currentReader);
        sub.setVipPackage(pkg);
        sub.setStartDate(Instant.now());
        sub.setEndDate(Instant.now().plus(pkg.getDurationDays(), java.time.temporal.ChronoUnit.DAYS));
        sub.setRemainingJobs(pkg.getMaxJobsPerDay()); // Tổng lượt của gói
        sub.setStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(sub);

        System.out.println("Kích hoạt gói " + pkg.getName() + " thành công cho: " + currentReader.getUsername());
    }

    // Hàm dành riêng cho Admin xem danh sách
    public List<SubscriptionAdminResponse> getAllSubscriptionsForAdmin() {
        return subscriptionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(sub -> new SubscriptionAdminResponse(
                        sub.getId(),
                        sub.getReader().getUsername(),
                        sub.getReader().getFullName(),
                        sub.getVipPackage().getName(),
                        sub.getVipPackage().getPrice(),
                        sub.getStartDate(),
                        sub.getEndDate(),
                        sub.getRemainingJobs(),
                        sub.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void activateSubscriptionByReaderId(Long readerId, Integer packageId) {
        // 1. Tìm Reader từ Database (Vì VNPay gọi ngầm nên phải load lại từ DB)
        User reader = userRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader ID: " + readerId));

        // 2. Tìm gói VIP mà Reader đã chọn mua
        VipPackage vipPackage = vipPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Gói VIP không tồn tại ID: " + packageId));

        // 3. Xử lý gói cũ: Nếu đang có gói ACTIVE thì chuyển thành EXPIRED
        // (Để tránh việc một người có 2 gói chạy song song gây loạn lượt nhận khách)
        subscriptionRepository.findValidSubscription(readerId).ifPresent(oldSub -> {
            oldSub.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(oldSub);
        });

        // 4. Khởi tạo "hợp đồng" VIP mới
        Subscription newSub = new Subscription();
        newSub.setReader(reader);
        newSub.setVipPackage(vipPackage);
        newSub.setStartDate(Instant.now());
        newSub.setEndDate(Instant.now().plus(vipPackage.getDurationDays(), java.time.temporal.ChronoUnit.DAYS));
        newSub.setStatus(SubscriptionStatus.ACTIVE);

        // Nạp tổng lượt nhận khách theo cấu hình gói
        newSub.setRemainingJobs(vipPackage.getMaxJobsPerDay());

        subscriptionRepository.save(newSub);

        System.out.println(">>> [VNPay] Đã kích hoạt gói " + vipPackage.getName() + " cho User: " + reader.getUsername());
    }
}
