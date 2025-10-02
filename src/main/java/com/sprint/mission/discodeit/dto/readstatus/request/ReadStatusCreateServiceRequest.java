package com.sprint.mission.discodeit.dto.readstatus.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadStatusCreateServiceRequest {

    private final UUID channelId;
    private final UUID userId;

    public ReadStatus toEntity(User user, Channel channel) {
        return ReadStatus.builder()
                .user(user)
                .channel(channel)
                .build();
    }
}
