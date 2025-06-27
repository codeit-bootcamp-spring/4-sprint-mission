package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends BaseEntity {

    private final UUID channelId;
    private final UUID userId;

    public ReadStatus(UUID channelId, UUID userId) {
        this.channelId = channelId;
        this.userId = userId;
    }
}