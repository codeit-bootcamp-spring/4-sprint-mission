package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

@Schema(description = "변경할 User 온라인 상태 정보")
public record UserStatusUpdateRequest(
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "이메일의 형식이 아닙니다.")
        String email,
        @PastOrPresent(message = "온라인 상태는 과거이거나 현재여야 합니다.")
        Instant newLastActiveAt
) {
}
