package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Service.VipPackage.VipPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reader/vip-packages")
@RequiredArgsConstructor
public class ReaderVipPackageController {

    private final VipPackageService vipPackageService;

    // Reader xem danh sách các gói đang được bán
    @GetMapping
    public ResponseEntity<?> getAllPackages() {
        // Có thể dùng chung service với Admin hoặc viết thêm logic lọc
        // Ví dụ: Chỉ hiện các gói đang active (nếu ông có thêm trường is_active)
        return ResponseEntity.ok(vipPackageService.getAll());
    }

    // Xem chi tiết 1 gói khi Reader bấm vào để xem "Benefits"
    @GetMapping("/{id}")
    public ResponseEntity<?> getPackageDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(vipPackageService.getById(id));
    }
}
