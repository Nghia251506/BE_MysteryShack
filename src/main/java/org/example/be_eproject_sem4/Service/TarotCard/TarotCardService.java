package org.example.be_eproject_sem4.Service.TarotCard;

import org.example.be_eproject_sem4.Dto.Tarot.DrawTarotRequest;
import org.example.be_eproject_sem4.Dto.Tarot.DrawTarotResponse;
import org.example.be_eproject_sem4.Dto.Tarot.InterpretSelectedRequest;
import org.example.be_eproject_sem4.Dto.TarotCard.CreateTarotCardDto;
import org.example.be_eproject_sem4.Dto.TarotCard.TarotCardResponseDto;
import org.example.be_eproject_sem4.Dto.TarotCard.UpdateTarotCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TarotCardService {

    // Lấy tất cả lá bài (có phân trang cho admin)
    Page<TarotCardResponseDto> getAllCards(Pageable pageable, String arcana, String suit);

    // Lấy tất cả lá bài active (dùng cho user rút bài)
    List<TarotCardResponseDto> getAllActiveCards();

    // Lấy 1 lá bài theo ID
    TarotCardResponseDto getCardById(Long id);

    // Tìm theo số thứ tự (dùng khi cần lá bài cụ thể)
    TarotCardResponseDto getCardByCardNumber(Integer cardNumber);

    // Tìm theo tên tiếng Anh
    TarotCardResponseDto getCardByNameEn(String nameEn);

    // Tạo mới lá bài (admin)
    TarotCardResponseDto createCard(CreateTarotCardDto dto);

    // Cập nhật lá bài (admin)
    TarotCardResponseDto updateCard(Long id, UpdateTarotCardDto dto);

    // Xóa mềm (đổi isActive = false)
    void softDeleteCard(Long id);

    // Xóa cứng (nếu cần)
    void deleteCard(Long id);

    // Tìm kiếm lá bài active theo từ khóa (nameEn, nameVi, keyword)
    List<TarotCardResponseDto> searchActiveCards(String query);

    // Đếm tổng số lá bài
    long countAllCards();

    // Đếm số lá bài active
    long countActiveCards();
    DrawTarotResponse drawThreeCardsByTopic(String topic, LocalDate birthday);
    List<TarotCardResponseDto> shuffleAndGetDeck(DrawTarotRequest request);
    DrawTarotResponse interpretSelectedCards(String topic, LocalDate birthday, List<InterpretSelectedRequest.SelectedCard> selected);
}