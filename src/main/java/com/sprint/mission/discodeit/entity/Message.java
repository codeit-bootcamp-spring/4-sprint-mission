package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    @Setter
    private Instant updatedAt;
    //
    @Setter
    private String content;
    //
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
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

>>>>>>> fe56ded0b57ffcf4521001ca7a956c8f5baf981f
}
