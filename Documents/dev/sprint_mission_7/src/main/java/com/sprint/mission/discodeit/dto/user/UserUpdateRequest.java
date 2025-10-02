package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(name = "UserUpdateRequest")
public class UserUpdateRequest {
    private BinaryContentCreateDto binaryContent;

    private UUID userId;

    @Size(min = 2, max = 20, message = "유저명은 2~20자여야 합니다.")
    private String newUsername;

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String newEmail;

    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
}
