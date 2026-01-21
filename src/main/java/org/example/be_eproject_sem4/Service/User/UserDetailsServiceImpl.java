// package org.example.be_eproject_sem4.Service.User;

// import lombok.RequiredArgsConstructor;
// import org.example.be_eproject_sem4.Entity.User;
// import org.example.be_eproject_sem4.Repository.UserRepository;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import java.util.Collections;
// import java.util.HashSet;
// import java.util.Set;

// @Service
// @RequiredArgsConstructor
// public class UserDetailsServiceImpl implements UserDetailsService {

//     private final UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         User user = userRepository.findByUsername(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với username: " + username));

//         Set<GrantedAuthority> authorities = new HashSet<>();

//         // Thêm ROLE_ từ Role ENUM (ví dụ: ROLE_CUSTOMER hoặc ROLE_READER)
//         if (user.getRole() != null) {
//             String roleAuthority = "ROLE_" + user.getRole().name();
//             authorities.add(new SimpleGrantedAuthority(roleAuthority));
//         }

//         // Nếu sau này bạn thêm permissions cho role hoặc user riêng (tùy chọn)
//         // Ví dụ: nếu Role có Set<Permission> hoặc User có Set<Permission>
//         /*
//         if (user.getRole() != null && user.getRole().getPermissions() != null) {
//             user.getRole().getPermissions().forEach(p ->
//                     authorities.add(new SimpleGrantedAuthority(p.getCode()))
//             );
//         }
//         if (user.getPermissions() != null) {
//             user.getPermissions().forEach(p ->
//                     authorities.add(new SimpleGrantedAuthority(p.getCode()))
//             );
//         }
//         */

//         // Trả về UserDetails với:
//         // - username
//         // - passwordHash (đã hash)
//         // - isEnabled = isVerified (chỉ tài khoản đã verify mới login được)
//         // - Các authorities (ROLE_ + permissions nếu có)
//         return new org.springframework.security.core.userdetails.User(
//                 user.getUsername(),
//                 user.getPasswordHash(),  // ← Sửa: dùng passwordHash thay password
//                 user.isVerified(),       // ← isEnabled: chỉ tài khoản verified mới active
//                 true,                    // accountNonExpired
//                 true,                    // credentialsNonExpired
//                 true,                    // accountNonLocked
//                 authorities
//         );
//     }
// }