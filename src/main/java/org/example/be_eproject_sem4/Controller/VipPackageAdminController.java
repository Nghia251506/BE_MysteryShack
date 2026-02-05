package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.VipPackage.VipPackageDto;
import org.example.be_eproject_sem4.Dto.VipPackage.VipPackageSummaryDto;
import org.example.be_eproject_sem4.Service.VipPackage.VipPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/vip-packages")
@RequiredArgsConstructor
public class VipPackageAdminController {
    private final VipPackageService service;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody VipPackageDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody VipPackageDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/summary")
    public ResponseEntity<VipPackageSummaryDto> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }

    // 2. API bật/tắt trạng thái gói (Active/Inactive)
    @PatchMapping("/{id}/status")
    public ResponseEntity<VipPackageDto> toggleStatus(@PathVariable Integer id) {
        VipPackageDto updatedPkg = service.toggleStatus(id);
        return ResponseEntity.ok(updatedPkg);
    }
}
