package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "메시지 생성 정보")
public record MessageCreateRequest(
    @Schema(description = "메시지 내용")
    String content,

    @Schema(description = "메시지를 보낼 채널의 고유 ID")
    UUID channelId,

    @Schema(description = "메시지 작성자의 고유 ID")
    UUID authorId
) {

}
