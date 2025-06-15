package com.sprint.mission.discodeit.entity;

import java.lang.reflect.Array;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private final List<Message> messages;
    private final List<Channel> channels;

    public User(String name) {
        super();
        this.username = name;

        //추가
        this.messages = new ArrayList<Message>();
        this.channels = new ArrayList<Channel>();
    }

    public String getUsername() {
        return username;
    }

    public void updateName(String userName){
        this.username = userName;
    }

    //유저의 Message 목록에 추가
    public void addMessage(Message message){
        if(!messages.contains(message)) {
            messages.add(message);
            message.addUser(this);
        }
    }

    //유저의 Channel 목록에 추가
    public void addChannel(Channel channel){
        if(!channels.contains(channel)) {
            channels.add(channel);
            channel.addUser(this);
        }
    }

    public void deleteChannel(Channel channel){
        if(!channels.contains(channel)){
            channels.remove(channel);
            channel.deleteUser(this);
        }
    }

    public void deleteMessage(Message message){
        if(!messages.contains(message)) {
            messages.remove(message);
            message.deleteUser(this);
        }
    }
    public List<Channel> getChannels(){
        return channels;
    }

    public List<Message> getMessages(){
        return messages;
    }
}