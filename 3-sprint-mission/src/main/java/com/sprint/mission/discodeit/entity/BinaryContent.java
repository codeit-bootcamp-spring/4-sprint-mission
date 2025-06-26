package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID messageId;
    private final Instant createdAt;
    @Setter private byte[] data;
    private final String dataType; // message인지 user인지 확인

    public BinaryContent(UUID userId, UUID messageId, byte[] data, String dataType) {
        this.userId = userId;
        this.messageId = messageId;
        this.createdAt = Instant.now();
        this.data = data;
        this.dataType = dataType;
        this.id = UUID.randomUUID();
    }
}
