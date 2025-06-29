package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;
    List<UUID> binaryIds;

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
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

    public void setBinaryContentIds(List<UUID> binaryContentIds) {
        this.binaryIds = binaryContentIds;
    }
=======
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private User user;
    private Channel channel;

    public Message(String content, User user, Channel channel) {
        super();
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }


    public void addUser(User user){
        this.user = user;
    }

    public void deleteUser(User user){
        this.user = user;
    }

    public void addChannel(Channel channel){
        this.channel = channel;
    }

    public void deleteChannel(Channel channel){
        this.channel = channel;
    }

    public void updateContent(String newContent){
        this.content = newContent;
        updateTimeStamp();
    }

>>>>>>> b9e39681b59d5df773deee8369d6d1b81958f2f6
}
