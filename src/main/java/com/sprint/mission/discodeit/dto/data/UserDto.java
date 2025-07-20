package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 정보 조회 시 사용되는 객체 (비밀번호 제외)")
public record UserDto(
    @Schema(description = "사용자 고유 ID")
    UUID id,

    @Schema(description = "생성 일시")
    Instant createdAt,

    @Schema(description = "마지막 업데이트 실시")
    Instant updatedAt,

    @Schema(description = "사용자 이름")
    String username,

    @Schema(description = "이메일 주소")
    String email,

    @Schema(description = "프로필 이미지 파일의 ID")
    UUID profileId,

    @Schema(description = "사용자 온라인 상태")
    Boolean online
) {

}
