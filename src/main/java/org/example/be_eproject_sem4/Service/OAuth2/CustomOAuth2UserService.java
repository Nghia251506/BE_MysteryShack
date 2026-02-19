package org.example.be_eproject_sem4.Service.OAuth2;

import java.util.Optional;

import org.example.be_eproject_sem4.Entity.User; // Thay đúng đường dẫn Model User của ông
import org.example.be_eproject_sem4.Entity.User.Role;
import org.example.be_eproject_sem4.Repository.UserRepository; // Thay đúng Repo của ông
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Xác định xem đang login bằng thằng nào (google hay facebook)
        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = "";

        // Xử lý lấy ảnh đại diện tùy theo Provider
        if ("facebook".equals(clientRegistrationId)) {
            // Facebook cấu hình picture theo dạng: picture { data { url } }
            java.util.Map<String, Object> pictureObj = oAuth2User.getAttribute("picture");
            if (pictureObj != null) {
                java.util.Map<String, Object> dataObj = (java.util.Map<String, Object>) pictureObj.get("data");
                if (dataObj != null) {
                    picture = (String) dataObj.get("url");
                }
            }
        } else {
            // Google mặc định là "picture"
            picture = oAuth2User.getAttribute("picture");
        }

        // Email của Facebook có thể null nếu đăng ký bằng SĐT, 
        // cần check để tránh lỗi NullPointerException
        if (email == null) {
            email = oAuth2User.getAttribute("id") + "@facebook.com"; // Fallback nếu không có email
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email);
            newUser.setFullName(name);
            newUser.setProfilePicture(picture);
            newUser.setVerified(true);
            newUser.setActive(true);
            newUser.setRole(Role.CUSTOMER);
            newUser.setPasswordHash("");
            userRepository.save(newUser);
        } else {
            User existingUser = userOptional.get();
            existingUser.setProfilePicture(picture);
            userRepository.save(existingUser);
        }

        return oAuth2User;
    }
}
