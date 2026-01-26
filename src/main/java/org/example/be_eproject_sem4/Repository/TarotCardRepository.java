package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.Arcana;
import org.example.be_eproject_sem4.Entity.TarotCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarotCardRepository extends JpaRepository<TarotCard, Long> {

    // Tìm theo số thứ tự (cardNumber) - dùng khi rút bài random
    Optional<TarotCard> findByCardNumber(Integer cardNumber);

    // Tìm theo tên tiếng Anh (unique)
    Optional<TarotCard> findByNameEnIgnoreCase(String nameEn);

    // Tìm theo Arcana (Đã rút gọn import vì đã có import ở trên)
    List<TarotCard> findByArcana(Arcana arcana);

    // Tìm theo suit (Minor Arcana)
    List<TarotCard> findBySuitIgnoreCase(String suit);

    // Chỉ lấy các lá bài active
    List<TarotCard> findByIsActiveTrue();

    /**
     * Tìm kiếm tổng quát theo tên Tiếng Anh hoặc Tiếng Việt
     * Đã loại bỏ tìm kiếm theo keyword để tránh lỗi Join ElementCollection
     */
    @Query("SELECT tc FROM TarotCard tc WHERE tc.isActive = true " +
           "AND (LOWER(tc.nameEn) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(tc.nameVi) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<TarotCard> searchActiveCards(@Param("query") String query);
}