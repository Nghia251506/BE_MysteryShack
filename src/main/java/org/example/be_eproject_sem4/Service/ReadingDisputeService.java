package org.example.be_eproject_sem4.Service;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.ReadingDispute.CreateDisputeRequest;
import org.example.be_eproject_sem4.Dto.ReadingDispute.ResolveDisputeRequest;
import org.example.be_eproject_sem4.Dto.ReadingDispute.DisputeResponse;
import org.example.be_eproject_sem4.Entity.ReadingDispute;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Mapper.DisputeMapper;
import org.example.be_eproject_sem4.Repository.ReadingDisputeRepository;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReadingDisputeService {

    private final ReadingDisputeRepository disputeRepository;
    private final ReadingSessionRepository sessionRepository;
    private final DisputeMapper disputeMapper;
    // private final WalletService walletService; // Giả định ông có service ví

    /**
     * 1. KHÁCH TẠO KHIẾU NẠI
     */
    @Transactional
    public DisputeResponse createDispute(CreateDisputeRequest request, User currentUser) {
        // Check xem phiên có tồn tại không
        ReadingSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên trải bài"));

        // Bảo mật: Chỉ người mua mới được kiện
        if (!session.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền khiếu nại phiên này!");
        }

        // Check xem đã khiếu nại chưa (tránh spam)
        disputeRepository.findBySessionId(request.getSessionId()).ifPresent(d -> {
            throw new RuntimeException("Phiên này đã đang trong quá trình khiếu nại!");
        });

        // Tạo Entity
        ReadingDispute dispute = new ReadingDispute();
        dispute.setSession(session);
        dispute.setCustomer(session.getCustomer());
        dispute.setReader(session.getReader());
        dispute.setReason(request.getReason());
        dispute.setEvidenceImages(request.getEvidenceImages());
        dispute.setStatus("PENDING");

        // Cập nhật trạng thái Session
        session.setStatus("DISPUTED");
        sessionRepository.save(session);

        return disputeMapper.toResponse(disputeRepository.save(dispute));
    }

    /**
     * 2. ADMIN LẤY DANH SÁCH (Phân trang + Filter)
     */
    public Page<DisputeResponse> getAllDisputes(String status, String keyword, Pageable pageable) {
        Page<ReadingDispute> disputes;
        if (keyword != null && !keyword.isEmpty()) {
            disputes = disputeRepository.searchDisputes(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            disputes = disputeRepository.findByStatus(status, pageable);
        } else {
            disputes = disputeRepository.findAll(pageable);
        }
        return disputes.map(disputeMapper::toResponse);
    }

    /**
     * 3. ADMIN XỬ LÝ TRANH CHẤP (QUYẾT ĐỊNH CUỐI CÙNG)
     */
    @Transactional
    public DisputeResponse resolveDispute(Long disputeId, ResolveDisputeRequest request) {
        ReadingDispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khiếu nại"));

        if (!"PENDING".equals(dispute.getStatus())) {
            throw new RuntimeException("Khiếu nại này đã được xử lý rồi!");
        }

        ReadingSession session = dispute.getSession();

        if ("RESOLVED_REFUND".equals(request.getStatus())) {
            // Nghiệp vụ Hoàn tiền
            // walletService.refundMoney(session.getCustomer().getId(), session.getAmount());
            
            session.setStatus("REFUNDED");
            dispute.setStatus("RESOLVED_REFUND");
        } else if ("RESOLVED_REJECT".equals(request.getStatus())) {
            // Bác bỏ khiếu nại
            session.setStatus("COMPLETED");
            dispute.setStatus("RESOLVED_REJECT");
        }

        dispute.setAdminNote(request.getAdminNote());
        dispute.setResolvedAt(Instant.now());

        sessionRepository.save(session);
        return disputeMapper.toResponse(disputeRepository.save(dispute));
    }
}