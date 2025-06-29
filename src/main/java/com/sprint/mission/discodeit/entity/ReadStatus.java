package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.DTO.ReadStatusCreateRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;


    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(ReadStatusCreateRequest readStatusCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = readStatusCreateRequest.userId();
        this.channelId = readStatusCreateRequest.channelId();
        this.lastReadAt = readStatusCreateRequest.lastReadAt();
    }

    public ReadStatus( UUID userId, UUID channelId ,Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }


    public void updateLastReadAt(Instant newTime) {
        this.lastReadAt = newTime;
        this.updatedAt = Instant.now();
    }

}
