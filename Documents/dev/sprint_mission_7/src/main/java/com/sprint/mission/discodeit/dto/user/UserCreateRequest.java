package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserCreateRequest", description = "User 생성 정보")
public class UserCreateRequest {

        @NotBlank(message = "유저명은 필수로 입력해주세요.")
        @Size(min = 2, message = "유저명은 2~20자 사이여야 합니다.")
        @Schema(description = "유저명")
        private String username;

        @NotBlank(message = "이메일은 필수로 입력해주세요.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        @Schema(description = "이메일 주소")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호")
        private String password;

        @Schema(type = "string", format = "binary")
        private MultipartFile file;

        private UserStatus status;
}
