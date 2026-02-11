package org.example.be_eproject_sem4.Service.VipPackage;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.VipPackage.SubscriptionAdminResponse;
import org.example.be_eproject_sem4.Dto.VipPackage.SubscriptionResponse;
import org.example.be_eproject_sem4.Entity.Payment;
import org.example.be_eproject_sem4.Entity.PaymentStatus;
import org.example.be_eproject_sem4.Entity.Subscription;
import org.example.be_eproject_sem4.Entity.SubscriptionStatus;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.VipPackage;
import org.example.be_eproject_sem4.Repository.PaymentRepository;
import org.example.be_eproject_sem4.Repository.SubscriptionRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Repository.VipPackageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
        private final SubscriptionRepository subscriptionRepository;
        private final VipPackageRepository vipPackageRepository;
        private final UserRepository userRepository;
        private final PaymentRepository paymentRepository;

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

                System.out.println(
                                "Kích hoạt gói " + pkg.getName() + " thành công cho: " + currentReader.getUsername());
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
                                                sub.getStatus().toString()))
                                .collect(Collectors.toList());
        }

        @Transactional
        public void activateSubscriptionByReaderId(Long readerId, Integer packageId, Map<String, String> vnpParams) {
                // 1. Load Data
                User reader = userRepository.findById(readerId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader ID: " + readerId));

                VipPackage vipPackage = vipPackageRepository.findById(packageId)
                                .orElseThrow(() -> new RuntimeException("Gói VIP không tồn tại ID: " + packageId));

                // 2. Logic Gia hạn/Cộng dồn
                java.util.Optional<Subscription> existingSub = subscriptionRepository.findValidSubscription(readerId);

                Instant newStartDate;
                Instant newEndDate;
                int newRemainingJobs;

                if (existingSub.isPresent()) {
                        Subscription oldSub = existingSub.get();
                        // CỘNG DỒN: Ngày bắt đầu mới là ngày hết hạn cũ
                        newStartDate = oldSub.getEndDate();
                        newEndDate = oldSub.getEndDate().plus(vipPackage.getDurationDays(), ChronoUnit.DAYS);
                        // Lượt nhận khách: Cộng dồn lượt cũ còn dư + lượt gói mới mua
                        newRemainingJobs = oldSub.getRemainingJobs() + vipPackage.getMaxJobsPerDay();

                        // Đánh dấu gói cũ là EXPIRED vì record mới sẽ đại diện cho tổng thời gian
                        oldSub.setStatus(SubscriptionStatus.EXPIRED);
                        subscriptionRepository.save(oldSub);
                } else {
                        // MUA MỚI
                        newStartDate = Instant.now();
                        newEndDate = newStartDate.plus(vipPackage.getDurationDays(), ChronoUnit.DAYS);
                        newRemainingJobs = vipPackage.getMaxJobsPerDay();
                }

                // 3. Tạo record Subscription mới
                Subscription newSub = new Subscription();
                newSub.setReader(reader);
                newSub.setVipPackage(vipPackage);
                newSub.setStartDate(newStartDate);
                newSub.setEndDate(newEndDate);
                newSub.setStatus(SubscriptionStatus.ACTIVE);
                newSub.setRemainingJobs(newRemainingJobs);
                subscriptionRepository.save(newSub);

                // 4. LƯU VÀO BẢNG PAYMENT (Dữ liệu cho Admin quản lý)
                if (vnpParams != null) {
                        Payment payment = new Payment();
                        payment.setTransactionNo(vnpParams.get("vnp_TransactionNo"));
                        payment.setTxnRef(vnpParams.get("vnp_TxnRef"));
                        payment.setAmount(Long.parseLong(vnpParams.get("vnp_Amount")) / 100);
                        payment.setBankCode(vnpParams.get("vnp_BankCode"));
                        payment.setOrderInfo(vnpParams.get("vnp_OrderInfo"));
                        payment.setStatus(PaymentStatus.SUCCESS);
                        payment.setUser(reader);
                        payment.setVipPackage(vipPackage);

                        // Parse vnp_PayDate (yyyyMMddHHmmss)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                        payment.setPaymentDate(LocalDateTime.parse(vnpParams.get("vnp_PayDate"), formatter));

                        paymentRepository.save(payment);
                }

                System.out.println(">>> [VNPay] Kích hoạt & Lưu Payment thành công cho: " + reader.getUsername());
        }

        public SubscriptionResponse getCurrentSubscription() {
                // 1. Lấy thông tin User đang login từ Security Context
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User currentReader = userRepository.findByUsername(auth.getName())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy reader"));

                // 2. Tìm gói ACTIVE và còn hạn (EndDate > Now)
                // Tôi giả định ông dùng DTO SubscriptionAdminResponse để trả về cho gọn,
                // hoặc ông có thể tạo DTO riêng là SubscriptionResponse
                return subscriptionRepository.findValidSubscription(currentReader.getId())
                                .map(sub -> new SubscriptionResponse(
                                                sub.getId(),
                                                sub.getReader().getUsername(),
                                                sub.getReader().getFullName(),
                                                sub.getVipPackage().getName(),
                                                sub.getVipPackage(), 
                                                sub.getVipPackage().getPrice(),
                                                sub.getStartDate(),
                                                sub.getEndDate(),
                                                sub.getRemainingJobs(),
                                                sub.getStatus().toString()))
                                .orElse(null); // Trả về null nếu chưa mua gói
        }
}
