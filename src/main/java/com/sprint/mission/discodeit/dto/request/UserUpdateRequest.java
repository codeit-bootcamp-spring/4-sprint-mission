package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 사용자 정보")
public record UserUpdateRequest(
    @Schema(description = "새 사용자 이름")
    String newUsername,

    @Schema(description = "새 이메일 주소")
    String newEmail,

    @Schema(description = "새 비밀번호")
    String newPassword
) {

}
