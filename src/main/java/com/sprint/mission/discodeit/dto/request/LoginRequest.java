package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "유저네임은 공백일 수 없습니다.")
    String username,

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    String password
) {

}
