package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 생성 정보")
public record UserCreateRequest(
    @Schema(description = "사용자 이름")
    String username,

    @Schema(description = "이메일 주소")
    String email,

    @Schema(description = "비밀번호")
    String password
) {

}
