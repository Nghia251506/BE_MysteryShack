package org.example.be_eproject_sem4.Service.History;

import org.example.be_eproject_sem4.Entity.History;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.HistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public Page<History> getRecentHistory(User currentUser, int page, int size) {
    // Thay vì fix cứng 0 và 10, ông nên nhận page và size từ Controller truyền xuống
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    return historyRepository.findRecentHistory(currentUser.getId(), pageable);
}
}
