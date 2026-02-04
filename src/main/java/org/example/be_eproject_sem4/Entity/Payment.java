package org.example.be_eproject_sem4.Entity;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vnp_transaction_no", unique = true)
    private String transactionNo; // Mã giao dịch của VNPay

    @Column(name = "vnp_txn_ref")
    private String txnRef; // Mã đơn hàng của mình gửi sang

    private Long amount;
    
    private String bankCode;
    
    private String orderInfo;
    
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // SUCCESS, FAILED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user; // Người thực hiện thanh toán

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id")
    private VipPackage vipPackage; // Gói đã mua
}
