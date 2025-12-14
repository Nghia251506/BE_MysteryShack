package org.example.be_eproject_sem4.Service.User;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Entity.Permission;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Thêm ROLE_ từ Role
        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()));
        }

        // Thêm các Permission của Role
        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            user.getRole().getPermissions().forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p.getPermission()))
            );
        }

        // Thêm các Permission riêng của User (override)
        if (user.getPermissions() != null) {
            user.getPermissions().forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p.getPermission()))
            );
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true, true, true,
                authorities
        );
    }
}