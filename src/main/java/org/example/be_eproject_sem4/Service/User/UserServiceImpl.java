// package org.example.be_eproject_sem4.Service.User;

// import lombok.RequiredArgsConstructor;
// import org.example.be_eproject_sem4.Dto.Auth.RegisterDto;
// import org.example.be_eproject_sem4.Dto.Auth.UserDto;
// import org.example.be_eproject_sem4.Dto.Auth.UserUpdateRequest;
// import org.example.be_eproject_sem4.Entity.Role;
// import org.example.be_eproject_sem4.Entity.User;
// import org.example.be_eproject_sem4.Mapper.UserMapper;
// import org.example.be_eproject_sem4.Repository.RoleRepository;
// import org.example.be_eproject_sem4.Repository.UserRepository;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// @Service
// @RequiredArgsConstructor
// public class UserServiceImpl implements UserService {

//     private final UserRepository userRepo;
//     private final RoleRepository roleRepo;
//     private final PasswordEncoder passwordEncoder;

//     // ============== CÁC HÀM TRONG INTERFACE ==============

//     @Override
//     @Transactional(readOnly = true)
//     public List<UserDto> getAll() {
//         return userRepo.findAll()
//                 .stream()
//                 .map(UserMapper::toDto)
//                 .toList();
//     }

//     @Transactional(readOnly = true)
//     @Override
//     public User findByUsername(String username) {
//         return userRepo.findByUsername(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<UserDto> getAllActiveUsers() {
//         return userRepo.findAll().stream()
//                 .filter(User::getIsActive)
//                 .map(UserMapper::toDto)
//                 .toList();
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public UserDto getById(Long id) {
//         User user = userRepo.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
//         return UserMapper.toDto(user);
//     }

//     @Override
//     @Transactional
//     public UserDto create(RegisterDto req) {
//         if (userRepo.existsByUsername(req.getUsername())) {
//             throw new IllegalArgumentException("Username đã tồn tại!");
//         }

//         User user = new User();
//         user.setUsername(req.getUsername());
//         user.setPassword(passwordEncoder.encode(req.getPassword()));
//         user.setFullname(req.getFullname());
//         user.setEmail(req.getEmail());

//         // Gán role mặc định nếu không truyền
//         Role defaultRole = req.getRole() != null
//                 ? roleRepo.findByRoleName(req.getRole())
//                 .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại: " + req.getRole()))
//                 : roleRepo.findByRoleName("CLIENT")
//                 .orElseThrow(() -> new RuntimeException("Không tìm thấy role CLIENT"));

//         user.setRole(defaultRole);
//         user.setIsActive(true);

//         User saved = userRepo.save(user);
//         return UserMapper.toDto(saved);
//     }

//     @Override
//     @Transactional
//     public UserDto update(Long id, UserUpdateRequest req) {
//         User user = userRepo.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

//         if (req.getFullName() != null && !req.getFullName().isBlank()) {
//             user.setFullname(req.getFullName());
//         }
//         if (req.getPassword() != null && !req.getPassword().isBlank()) {
//             user.setPassword(passwordEncoder.encode(req.getPassword()));
//         }
//         if (req.getRole() != null) {
//             Role role = roleRepo.findByRoleName(req.getRole())
//                     .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại: " + req.getRole()));
//             user.setRole(role);
//         }

//         User saved = userRepo.save(user);
//         return UserMapper.toDto(saved);
//     }

//     @Override
//     @Transactional
//     public void delete(Long id) {
//         if (!userRepo.existsById(id)) {
//             throw new IllegalArgumentException("User not found: " + id);
//         }
//         userRepo.deleteById(id);
//     }

//     // ============== CÁC HÀM PHỤ TRỢ CHO SECURITY ==============

//     @Transactional(readOnly = true)
//     public UserDto getByUsername(String username) {
//         User user = findByUsername(username);
//         return UserMapper.toDto(user);
//     }

//     @Transactional(readOnly = true)
//     public User getDomainUserByUsername(String username) {
//         return findByUsername(username);
//     }

//     /**
//      * Lấy thông tin user hiện tại đang đăng nhập (dùng trong controller/service)
//      */
//     @Transactional(readOnly = true)
//     public UserDto getCurrentUser() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//         // Nếu chưa đăng nhập hoặc là anonymous
//         if (authentication == null
//                 || !authentication.isAuthenticated()
//                 || authentication.getPrincipal() == null
//                 || "anonymousUser".equals(authentication.getPrincipal())) {
//             return null;
//         }

//         // Với Spring Security 6.3+, getName() vẫn hoạt động nếu dùng UserDetails
//         String username = authentication.getName();
//         return getByUsername(username);
//     }

//     @Transactional
//     public UserDto assignRoleToUser(Long userId, String roleCode) {
//         User user = userRepo.findById(userId)
//                 .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

//         Role role = roleRepo.findByRoleName(roleCode)
//                 .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleCode));

//         user.setRole(role);
//         User saved = userRepo.save(user);
//         return UserMapper.toDto(saved);
//     }
//     @Override
//     public String getFullNameByUsername(String username) {
//         return userRepo.findByUsername(username)
//                 .map(User::getFullname)
//                 .orElse("Unknown");
//     }
// }