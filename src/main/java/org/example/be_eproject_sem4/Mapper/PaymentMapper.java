package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.PaymentDTO;
import org.example.be_eproject_sem4.Entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "username" ,  source= "user.username")
    @Mapping(target = "packageName" ,  source= "vipPackage.name")
    @Mapping(target = "status",  source= "status")
    PaymentDTO toDTO(Payment payment);

    // Nếu ông cần map ngược lại từ Request/DTO sang Entity (ít dùng cho Payment vì dữ liệu từ VNPay)
    // @Mapping(target = "id", ignore = true)
    // Payment toEntity(PaymentDTO dto);
}
