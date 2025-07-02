package com.sprint.mission.discodeit.entity;
<<<<<<< HEAD

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    @Setter
    private Instant updatedAt;
    //
    private final ChannelType type;
    @Setter
    private String name;
    @Setter
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /*public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }*/


}
=======
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
>>>>>>> fe56ded0b57ffcf4521001ca7a956c8f5baf981f
