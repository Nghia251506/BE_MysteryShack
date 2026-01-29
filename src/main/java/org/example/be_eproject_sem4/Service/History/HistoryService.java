package org.example.be_eproject_sem4.Service.History;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Entity.*;
import org.example.be_eproject_sem4.Repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public List<History> getRecentHistory(User currentUser) {
        // Tạo Pageable: trang 0, lấy 10 phần tử, sắp xếp theo createdAt giảm dần
        Pageable topTen = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        return historyRepository.findRecentHistory(currentUser.getId(), topTen);
    }
}
