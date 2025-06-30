package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String content;

    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void changeAttachmentIds(List<UUID> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

}
