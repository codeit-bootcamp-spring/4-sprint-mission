package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record ChannelUpdateRequest(

        UUID channelId,
        String newName,
        String newDescription
) {
}
