package org.example.be_eproject_sem4.Service.Payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.be_eproject_sem4.Dto.PaymentDTO;
import org.example.be_eproject_sem4.Entity.Payment;
import org.example.be_eproject_sem4.Entity.PaymentStatus;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.VipPackage;
import org.example.be_eproject_sem4.Mapper.PaymentMapper;
import org.example.be_eproject_sem4.Repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    // 1. Hàm xử lý lưu Payment từ VNPay (Luồng Reader mua gói)
    public void processVnpayPayment(Map<String, String> vnpParams, User user, VipPackage vipPackage) {
        String transactionNo = vnpParams.get("vnp_TransactionNo");
        String txnRef = vnpParams.get("vnp_TxnRef");
        long amount = Long.parseLong(vnpParams.get("vnp_Amount")) / 100;
        String bankCode = vnpParams.get("vnp_BankCode");
        String responseCode = vnpParams.get("vnp_ResponseCode");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime payDate = LocalDateTime.parse(vnpParams.get("vnp_PayDate"), formatter);

        Payment payment = Payment.builder()
                .transactionNo(transactionNo)
                .txnRef(txnRef)
                .amount(amount)
                .bankCode(bankCode)
                .paymentDate(payDate)
                .user(user)
                .vipPackage(vipPackage)
                .status("00".equals(responseCode) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .orderInfo(vnpParams.get("vnp_OrderInfo"))
                .build();

        paymentRepository.save(payment);
    }

    // 2. Lấy danh sách cho Admin (Có phân trang để Controller gọi)
    public Page<PaymentDTO> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        return paymentRepository.findAll(pageable).map(paymentMapper::toDTO);
    }

    // 3. Lấy chi tiết giao dịch
    public PaymentDTO getPaymentDetail(Long id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại ID: " + id));
    }

    // 4. Thống kê doanh thu cho Admin "sướng"
    public Map<String, Object> getRevenueStats() {
        Map<String, Object> stats = new HashMap<>();
        Long totalRevenue = paymentRepository.getTotalRevenue();
        long totalTransactions = paymentRepository.count();
        
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0);
        stats.put("totalTransactions", totalTransactions);
        return stats;
    }
}