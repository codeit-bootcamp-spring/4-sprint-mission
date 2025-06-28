package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
@Setter
public class Message extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID channelId;
    private final UUID authorId;
    //
    private String content;
    private List<UUID> attachmentIds;

    public Message(UUID authorId, UUID channelId, String content) {
        super();
        //
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                ", attachmentIds=" + attachmentIds +
                '}';
    }

    public void update(String newContent, List<UUID> newAttachmentIds) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (newAttachmentIds != null && !newAttachmentIds.equals(this.attachmentIds)) {
            this.attachmentIds = newAttachmentIds;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
        }
    }
}
