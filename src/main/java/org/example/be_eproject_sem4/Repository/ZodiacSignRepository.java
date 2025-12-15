package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.ZodiacDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ZodiacSignRepository extends JpaRepository<ZodiacDaily, Long> {
    // Tìm cung theo tên tiếng Việt hoặc tiếng Anh (unique)
    Optional<ZodiacDaily> findByNameViIgnoreCase(String nameVi);
    Optional<ZodiacDaily> findByNameEnIgnoreCase(String nameEn);

    // Tìm cung theo ngày sinh (ngày nằm giữa startDate và endDate)
    @Query("SELECT z FROM ZodiacDaily z " +
            "WHERE (MONTH(:birthDate) = MONTH(z.startDate) AND DAY(:birthDate) >= DAY(z.startDate)) " +
            "OR (MONTH(:birthDate) = MONTH(z.endDate) AND DAY(:birthDate) <= DAY(z.endDate)) " +
            "OR (MONTH(z.startDate) > MONTH(z.endDate) AND " +
            "    (MONTH(:birthDate) > MONTH(z.startDate) OR MONTH(:birthDate) < MONTH(z.endDate))) " +
            "AND z.active = true")
    Optional<ZodiacDaily> findByBirthDate(LocalDate birthDate);

    // Tìm tất cả cung active (dùng cho admin list)
    List<ZodiacDaily> findAllByActiveTrue();

    // Kiểm tra trùng tên khi create/update
    boolean existsByNameViIgnoreCaseAndIdNot(String nameVi, Long id);
    boolean existsByNameEnIgnoreCaseAndIdNot(String nameEn, Long id);
}
