package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.TarotCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarotCardRepository extends JpaRepository<TarotCard, Long> {

    // Tìm theo số thứ tự (cardNumber) - dùng khi rút bài random
    Optional<TarotCard> findByCardNumber(Integer cardNumber);

    // Tìm theo tên tiếng Anh (unique)
    Optional<TarotCard> findByNameEnIgnoreCase(String nameEn);

    // Tìm theo Arcana
    List<TarotCard> findByArcana(org.example.be_eproject_sem4.Entity.Arcana arcana);

    // Tìm theo suit (Minor Arcana)
    List<TarotCard> findBySuitIgnoreCase(String suit);

    // Tìm kiếm theo keyword trong Set keywords
    @Query("SELECT tc FROM TarotCard tc JOIN tc.keywords k WHERE LOWER(k) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TarotCard> findByKeywordContainingIgnoreCase(String keyword);

    // Chỉ lấy các lá bài active
    List<TarotCard> findByIsActiveTrue();

    // Tìm kiếm tổng quát (nameEn, nameVi, keyword)
    @Query("SELECT tc FROM TarotCard tc WHERE tc.isActive = true " +
            "AND (LOWER(tc.nameEn) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(tc.nameVi) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR EXISTS (SELECT 1 FROM tc.keywords k WHERE LOWER(k) LIKE LOWER(CONCAT('%', :query, '%'))))")
    List<TarotCard> searchActiveCards(String query);
}