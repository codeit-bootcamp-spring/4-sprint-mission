package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID channelId;
    private final UUID authorId;
    private final Instant createdAt;
    private Instant updatedAt;
    private String content;
    private List<UUID> attachmentIds; //BinaryContent Id 리스트

    public Message(UUID id, UUID authorId, UUID channelId, String content, List<UUID> attachmentIds, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = attachmentIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public void updateAttachmentIds(List<UUID> attachmentId) {
        this.attachmentIds = new ArrayList<>(attachmentId);
        this.updatedAt = Instant.now();
    }
}
