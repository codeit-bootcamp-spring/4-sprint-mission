package com.sprint.mission.discodeit.entity;

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

}
