package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastMessageSentAt; // 가장 최근 메시지의 시간

    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastMessageSentAt = this.createdAt;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    // 공개채널
    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    // 메시지 보낼 때마다 시간 갱신
    public void updateLastMessageSentAt() {
        this.lastMessageSentAt = Instant.now();
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

}
