package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotEmpty(message = "채널에 참가할 사용하는 두명입니다.")
    @Size(min = 2, max = 2, message = "채널에 참가할 사용하는 두명입니다.")
    List<UUID> participantIds
) {

}
