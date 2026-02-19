package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.Admin.ReaderManagerResponse;
import org.example.be_eproject_sem4.Dto.Auth.RegisterRequestDto;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Dto.Auth.UserUpdateDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.User.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Map từ RegisterRequest → User entity (khi đăng ký)
    public User toEntity(RegisterRequestDto request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(request.getPassword()) // Lưu ý: password sẽ được hash sau ở service
                .fullName(request.getFullName())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .role(Role.CUSTOMER) // Mặc định là CUSTOMER khi đăng ký
                .isVerified(false) // Chưa verify
                .eloScore(500) // Default ELO cho reader mới (nếu sau này upgrade)
                .build();
    }

    // Map từ UserUpdateDto → User entity (cập nhật thông tin)
    public void updateUserFromDto(UserUpdateDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }
        if (dto.getQrCode() != null) {
            user.setQRCode(dto.getQrCode());
        }
        // Không cho update role, email, username (trừ admin)
    }

    // Nếu bạn cần map User → UserDto (cho admin hoặc list user)
    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        // KHÔNG map passwordHash
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setBirthDate(user.getBirthDate());
        dto.setRole(user.getRole().name()); // Hoặc user.getRole()
        dto.setBio(user.getBio());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setQrCode(user.getQRCode());
        dto.setVerified(user.isVerified());
        dto.setActive(user.isActive());
        dto.setEloScore(user.getEloScore());
        dto.setBlocked(user.getIsBlocked());
        dto.setBusy(user.getIsBusy());
        return dto;
    }

    // Nếu cần map từ LoginRequest (không cần entity, chỉ dùng để validate)
    // Thường không map LoginRequest → User, chỉ dùng trong service để authenticate
    public ReaderManagerResponse toReaderManagerResponse(User user, Double avgRating, Integer completedSessions, Integer totalPurchases) {
        if (user == null) {
            return null;
        }

        return ReaderManagerResponse.builder()
                .id(String.valueOf(user.getId()))
                .fullName(user.getFullName())
                .email(user.getEmail())
                .account(user.getUsername())
                .phone(user.getPhone())
                .dob(user.getBirthDate() != null ? user.getBirthDate().toString() : null)
                .address(null) // Nếu entity User có field address thì map vào đây
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .avatar(user.getProfilePicture()) // Đồng bộ cho FE dễ dùng
                // Logic map Status từ IsBlocked và IsBusy
                .status(user.getIsBlocked() ? "Locked" : (user.getIsBusy() ? "Busy" : user.isActive() ? "Online" : "Offline"))
                .eloScore(user.getEloScore())
                .createdAt(user.getCreatedAt()) // Giả định ông có field này
                // Các trường lấy từ các Repo khác truyền vào
                .rating(avgRating != null ? avgRating : 0.0)
                .completedSessions(completedSessions != null ? completedSessions : 0)
                .totalPurchases(totalPurchases != null ? totalPurchases : 0)
                .build();
    }
}
