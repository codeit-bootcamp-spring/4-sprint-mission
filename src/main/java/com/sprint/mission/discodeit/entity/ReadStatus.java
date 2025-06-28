package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID userId;
    private UUID channelId;
    private Instant lastReadTime;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = Instant.ofEpochSecond(Instant.now().getEpochSecond());
    }

    public void update(Instant newLastReadTime) {
        boolean anyValueUpdated = false;

        if (newLastReadTime != null && !newLastReadTime.equals(lastReadTime)) {
            this.lastReadTime = newLastReadTime;
            anyValueUpdated  = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
        }
    }
}
