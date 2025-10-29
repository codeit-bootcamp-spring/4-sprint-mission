package com.sprint.mission.discodeit.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "유저 네임은 null이거나 공백이면 안됨")
    private String newUsername;

    @Email(message = "이메일 형식이 올바르지 않음")
    private String newEmail;

    //선택적으로 들어오는 필드라 null 허용해야함
    private String newPassword;

    public UserUpdateServiceRequest toServiceRequest(UUID userId, MultipartFile profile) {
        return UserUpdateServiceRequest.builder()
                .userId(userId)
                .newUsername(newUsername)
                .newEmail(newEmail)
                .newPassword(newPassword)
                .profile(profile)
                .build();
    }
}
