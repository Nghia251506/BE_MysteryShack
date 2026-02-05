package org.example.be_eproject_sem4.Service.Admin;

import org.example.be_eproject_sem4.Dto.Admin.AdjustEloRequestDto;
import org.example.be_eproject_sem4.Dto.Admin.UpdateReaderStatusDto;
import org.example.be_eproject_sem4.Entity.EloHistory;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.EloHistoryRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReaderAdminService {
    private final UserRepository userRepository; // Reader thực chất là User role READER
    private final EloHistoryRepository eloHistoryRepository;

    @Transactional
    public void adjustElo(Long readerId, AdjustEloRequestDto dto, String adminName) {
        User reader = userRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader"));

        double oldElo = reader.getEloScore();
        double newElo = oldElo + dto.getAmount();

        // 1. Cập nhật ELO cho Reader
        reader.setEloScore(newElo);
        userRepository.save(reader);

        // 2. Lưu lịch sử biến động
        EloHistory history = new EloHistory();
        history.setReaderId(readerId);
        history.setBeforeElo((int) oldElo);
        history.setAfterElo((int) newElo);
        history.setChangeAmount(dto.getAmount());
        history.setReason(dto.getReason());
        history.setType(dto.getType());
        history.setAdminUsername(adminName);
        
        eloHistoryRepository.save(history);
    }

    // @Transactional
    // public void updateStatus(Long readerId, UpdateReaderStatusDto dto) {
    //     User reader = userRepository.findById(readerId)
    //             .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader"));
        
    //     // Logic duyệt Reader: Nếu từ PENDING sang ACTIVE thì có thể set ELO mặc định
    //     if ("PENDING".equals(reader.getStatus()) && "ACTIVE".equals(dto.getStatus())) {
    //         reader.setElo(1000); // Điểm khởi đầu mặc định
    //     }

    //     reader.setStatus(dto.getStatus());
    //     // Có thể lưu thêm note lý do vào một bảng UserLog nếu cần
    //     userRepository.save(reader);
    // }
}
