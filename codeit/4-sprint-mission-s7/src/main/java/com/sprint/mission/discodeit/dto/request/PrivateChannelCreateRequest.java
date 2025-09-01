package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotEmpty(message = "참가자 목록은 비어 있을 수 없습니다")
    List<@NotNull(message = "participantIds 항목은 null일 수 없습니다") UUID> participantIds
) {

}
