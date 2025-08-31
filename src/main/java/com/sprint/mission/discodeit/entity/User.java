package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BasedEntity{

    private String userName;
    private final List<Channel> channels;
    private final List<Message> messages;
    private String email;
    private String password;
    private Status status;

    public User(String userName, String password, String email) {
        super();
        this.userName = userName;
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.email = email;
        this.password = password;
        this.status = Status.ONLINE;
    }

    /********************
     * 유저 업데이트를 위해 status를 추가한 생성자
     * 유저는 이름, 패스워드, 이메일, 상태를 변경할 수 있다.
     * @param userName  유저의 이름
     * @param password  유저의 패스워드
     * @param email  유저의 이메일
     * @param status  유저의 상태 / enum 클래스 사용
     ********************/
    public User(String userName, String password, String email, Status status) {
        super();
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = status;
    }
    @Override
    public String toString() {
        return "User{" +
                "userId=" + getId() +
                ", userCreatedAt=" + getCreatedAt() +
                ", userUpdatedAt=" + getUpdatedAt() +
                ", userName='" + userName + '\'' +
                ", email= " + email +
                ", password= " + password +
                ", channels=" + getChannelNames() +  // 유저가 존재하는 채널의 이름 리스트
                ", messages=" + getMessageContents() +  // 유저가 작성한 메시지의 내용 리스트
                ", status=" + status.getValue() +  // status.getValue()로 상태 값을 변수명이 아닌 변수의 값을 받는다
                '}';
    }


    /**
     * getter
     */

    public String getUserName() {
        return userName;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<String> getChannelNames() {
        return channels.stream().map(Channel::getChannelName)
                .toList();
    }

    public List<String> getMessageContents() {
        return messages.stream().map(Message::getContent)
                .toList();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() { return password; }

    public Status getStatus() { return status; }

    /**
     * setter
     */

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setEmail(String email) { this.email = email; }

    /**
     * add, delete
     */
    public void addChannel(Channel channel){
        if (!channels.contains(channel)) {  // 채널이 유저의 채널리스트에 존재하지 않으며
            channels.add(channel);
            channel.addUser(this);
        }
    }

    public void addMessage(Message message){
        if(!messages.contains(message) && message.getUser().equals(this))  {  // 메시지가 유저의 메시지리스트에 존재하지 않으며 메시지의 작성자가 동일해야 한다
            messages.add(message);
            message.addUser(this);
        }
    }

    public void removeChannel(Channel channel) {
        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.removeUser(this);
        }
    }
    public void removeMessage(Message message){
        if(messages.contains(message)) {
            messages.remove(message);
            message.removeUser(this);
        }
    }
}
