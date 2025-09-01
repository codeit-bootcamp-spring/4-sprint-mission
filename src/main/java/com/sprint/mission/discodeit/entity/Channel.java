package com.sprint.mission.discodeit.entity;
import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private String channel;
    private final List<Message> messages;
    private final List<User> users;

    public Channel(String channel) {
        super();
        this.channel = channel;

        this.messages = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public String getChannel() {
        return channel;
    }

    public void updateChannel(String updateChannel){
        this.channel = updateChannel;
        updateTimeStamp();
    }

    //추가
    public void addUser(User user){
        if(!users.contains(user)) {
            users.add(user);
            user.addChannel(this);
        }
    }
    //추가
    public void addMessage(Message message){
        if(!messages.contains(message)) {
            messages.add(message);
            message.addChannel(this);
        }
    }
    public void deleteMessage(Message message){
        if(!messages.contains(message)){
            messages.remove(message);
            message.deleteChannel(this);
        }
    }

    public void deleteUser(User user){
        if(!users.contains(user)){
            users.remove(user);
            user.deleteChannel(this);
        }
    }

    //추가
    public List<Message> getMessages(){
        return messages;
    }
}