package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserState;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private final List<Channel> channels =  new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private String userName;
    private UserState state;

    @Override
    public String toString() {
        String channelNames = channels.stream()
                .map(Channel::getChannelName)
                .collect(Collectors.joining(", "));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String created = sdf.format(new Date(getCreatedAt()));
        String updated = sdf.format(new Date(getUpdatedAt()));

        return "User{" +"\n"+
                "userName=" + userName +"\n"+
                "userState=" + state +"\n"+
                "channels=" + channelNames +"\n"+
                "createdAt=" + created +"\n"+
                "updatedAt=" + updated +"\n"+
                '}'+"\n";
    }

    public User(String userName) {
        this.userId = UUID.randomUUID();
        this.userName = userName;
        state = UserState.ONLINE;
    }

    //양방향 연결
    public void addChannel(Channel channel) {
        if(!channels.contains(channel)) {
            channels.add(channel);
            channel.addUser(this);
        }
    }
    public void removeChannel(Channel channel) {
        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.removeUser(this);
        }
    }

    public void addMessage(Message message) {
        if(!messages.contains(message)) {
            messages.add(message);
            message.addUser(this);
        }
    }
    public void removeMessage(Message message) {
        if(messages.contains(message)) {
            messages.remove(message);
            message.removeUser(this);
        }
    }

    //상태
    public void onlineUserState() {
        state = UserState.ONLINE;
    }
    public void offlineUserState() {
        state = UserState.OFFLINE;
    }
    public void deletedUserState() {
        state = UserState.DELETED;
    }


    public void removeAllMessages(){
        messages.clear();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserState getState() {
        return state;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Channel> getChannels() {
        return channels;
    }

}

