package org.example.be_eproject_sem4.Service.VipPackage;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.VipPackage.VipPackageDto;
import org.example.be_eproject_sem4.Entity.VipPackage;
import org.example.be_eproject_sem4.Repository.VipPackageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VipPackageService {
    private final VipPackageRepository repository;

    public List<VipPackage> getAll() {
        return repository.findAll();
    }

    public VipPackage create(VipPackageDto dto) {
        VipPackage pkg = new VipPackage();
        BeanUtils.copyProperties(dto, pkg);
        return repository.save(pkg);
    }

    public VipPackage update(Integer id, VipPackageDto dto) {
        VipPackage pkg = repository.findById(id).orElseThrow();
        pkg.setName(dto.getName());
        pkg.setPrice(dto.getPrice());
        pkg.setDurationDays(dto.getDurationDays());
        pkg.setBenefits(dto.getBenefits());
        // update các trường khác...
        return repository.save(pkg);
    }

    public void delete(Integer id) {
        // Lưu ý: Sau này phải check xem có ai đang sub gói này không trước khi xóa
        repository.deleteById(id);
    }

    public VipPackage getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gói VIP không tồn tại!"));
    }
}
