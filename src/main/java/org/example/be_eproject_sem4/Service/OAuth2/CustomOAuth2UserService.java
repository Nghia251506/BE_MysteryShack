package org.example.be_eproject_sem4.Service.OAuth2;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Entity.User; // Thay đúng đường dẫn Model User của ông
import org.example.be_eproject_sem4.Entity.User.Role;
import org.example.be_eproject_sem4.Repository.UserRepository; // Thay đúng Repo của ông
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Bóc tách dữ liệu từ Google trả về
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // Kiểm tra xem user này đã tồn tại trong DB chưa
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Nếu chưa có thì tạo mới
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email);
            newUser.setFullName(name);
            newUser.setProfilePicture(picture);
            newUser.setVerified(true); // Google đã verify email rồi nên mình set true luôn
            newUser.setActive(true);
            newUser.setRole(Role.CUSTOMER); // Mặc định là CUSTOMER
            newUser.setPasswordHash(""); // Login qua Google thì không cần pass, hoặc ông để pass random
            userRepository.save(newUser);
        } else {
            // Nếu có rồi thì cập nhật lại ảnh đại diện hoặc tên nếu cần
            User existingUser = userOptional.get();
            existingUser.setProfilePicture(picture);
            userRepository.save(existingUser);
        }

        return oAuth2User;
    }
}