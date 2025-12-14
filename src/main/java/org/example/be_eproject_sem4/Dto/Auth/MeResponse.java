package org.example.be_eproject_sem4.Dto.Auth;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeResponse {
    private boolean success = true;
    private String message = "Lấy thông tin thành công";
    private String username;
    private String fullname;
    private String role;
    private Set<String> permissions;
    private boolean active;
}