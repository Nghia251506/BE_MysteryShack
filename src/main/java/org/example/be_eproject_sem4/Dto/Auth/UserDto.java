package org.example.be_eproject_sem4.Dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;
import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private Date birthDate;
    private boolean isVerified;
    private double eloScore;
    private String bio;
    private String profilePicture;
    private String qrCode;
    private boolean isActive;
    private List<ReadingSessionSimpleDto> matchedSessions;
}