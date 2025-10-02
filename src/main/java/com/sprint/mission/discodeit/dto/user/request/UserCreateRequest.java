package com.sprint.mission.discodeit.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @Length(min = 1, max = 50, message = "유저 네임은 1 ~ 50자 이어야함")
    @NotBlank(message = "유저 네임은 null이거나 공백이면 안됨")
    private String username;

    @Length(min = 1, max = 100, message = "이메일은 1 ~ 100자 이어야함")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않음")
    private String email;

    @Length(min = 1, max = 60, message = "비밀번호는 1 ~ 60자 이어야함")
    @NotBlank(message = "비밀번호는 null이거나 공백이면 안됨")
    private String password;

    private MultipartFile profile;

    public UserCreateServiceRequest toServiceRequest(MultipartFile profile) {
        return UserCreateServiceRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .profile(profile)
                .build();
    }
}
