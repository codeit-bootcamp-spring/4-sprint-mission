package com.sprint.mission.discodeit.entity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends baseEntity {
    private UUID channelId;
    private String channelName;
    private List<User> users;
    private List<Message> messages;

    public Channel(String channelName) {
        this.channelId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.channelName = channelName;
        messages = new ArrayList<Message>();
        users = new ArrayList<User>();
    }

    public UUID getChannelId() { return channelId; }

    public String getChannelName() { return channelName ; }

    public List<User> getUsers() { return users; }

    public List<Message> getMessages() { return messages; }

    public void addUser(User newUser) {
        if(!users.contains(newUser)) {
            users.add(newUser);
            newUser.addChannel(this);
        }
    }
    public void removeUser(User deletedUser) {
        if(users.contains(deletedUser)) {
            users.remove(deletedUser);
            deletedUser.removeChannel(this);
        }
    }

    public void addMessage(Message m) {
        this.messages.add(m);
    }

    public void removeMessage(Message deletedMessage){
            this.messages.remove(deletedMessage);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }



}
