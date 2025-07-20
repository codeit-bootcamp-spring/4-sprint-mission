package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "사용자명")
        private String username;

        @Schema(description = "이메일 주소")
        private String email;

        @Schema(description = "비밀번호")
        private String password;

        @Schema(type = "string", format = "binary")
        private MultipartFile file;
}
