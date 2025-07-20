package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(description = "사용자 이름")
    String username,
    @Schema(description = "비밀번호")
    String password
) {

}
