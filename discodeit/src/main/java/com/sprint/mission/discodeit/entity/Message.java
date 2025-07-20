package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private List<BinaryContent> attachments;

    public Message(UUID id, UUID authorId, UUID channelId, String content, List<BinaryContent> attachments) {
        this.id = id;
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.attachments = attachments;
        this.createdAt = Instant.now();
    }

    public void loadAttachments(List<BinaryContent> files) {
        this.attachments = files;
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

    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = Instant.now();
    }
}
