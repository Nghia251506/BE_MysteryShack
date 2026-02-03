package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.VipPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VipPackageRepository extends JpaRepository<VipPackage, Integer> {

    // Tìm gói theo tên (dùng để check trùng tên khi Admin tạo mới)
    boolean existsByName(String name);

    // Tìm các gói có giá trong khoảng (Dành cho bộ lọc sau này)
    List<VipPackage> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Tìm gói theo thời gian sử dụng (Ví dụ: Gói 30 ngày)
    List<VipPackage> findByDurationDays(Integer days);
}
