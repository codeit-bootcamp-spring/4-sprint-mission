package com.sprint.mission.discodeit.entity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends baseEntity{

    public enum Status{
        ACTIVE,
        INACTIVE,
        DELETED;
    }

    private UUID userId;
    private String userName;
    private List<Channel> channels;
    private List<Message> messages;
    private Status status;

    public User(String userName) {
        super();
        this.userId =UUID.randomUUID();
        this.userName = userName;
        this.status = status.ACTIVE;
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
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

    public List<Message> getMessages() {
        return messages;
    }

    public void updateUserName(String newUserName) {
       this.userName = newUserName;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public void addChannel(Channel newChannel){
        if(!channels.contains(newChannel)) {
            channels.add(newChannel);
            newChannel.addUser(this);
        }
    }

    public void removeChannel(Channel newChannel){
        if(channels.contains(newChannel)) {
            channels.remove(newChannel);
            newChannel.removeUser(this);
        }
    }

    public void addMessage(Message newMessage){
        this.messages.add(newMessage);
    }

    public void removeMessage(Message m){ this.messages.remove(m); }

}
