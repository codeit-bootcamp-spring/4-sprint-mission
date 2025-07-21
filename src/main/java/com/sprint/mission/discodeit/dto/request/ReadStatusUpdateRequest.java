package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ReadStatusUpdateRequest(

    @Schema(description = "새로운 마지막 읽음 시간")
    Instant newLastReadAt
) {

}
