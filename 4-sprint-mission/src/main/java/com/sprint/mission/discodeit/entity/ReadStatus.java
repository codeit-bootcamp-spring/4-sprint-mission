package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ReadStatus extends BaseEntity {
    UUID userId;
    UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    public List<Message> hasUnreadMessages(List<Message> messages) {
        return messages.stream()
                .filter(message -> message.getCreatedAt().isAfter(lastReadAt))
                .collect(Collectors.toList());
    }

    public void updateReadTime() {
        this.lastReadAt = Instant.now();
        setUpdatedAt();
    }
}
