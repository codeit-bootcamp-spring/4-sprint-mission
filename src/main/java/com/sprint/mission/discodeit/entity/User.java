package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.domain.UserType;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class User extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String userName;
    private UserType.UserStatus userStatus;

    private List<Channel> channels = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public User(String userName) {
        super();
        this.userName = userName;
        this.userStatus = UserType.UserStatus.ACTIVE;
    }

    // Getter/Setter
    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public UserType.UserStatus getStatus() {
        return userStatus;
    }

    public void setStatus(UserType.UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "userId=" + super.getId() +
                ", userName='" + userName + '\'' +
                ", userStatus=" + userStatus +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                "}\n";
    }

    // 채널 관련 메서드
    // 매개변수로 받은 channel 인수 추가
    public void addChannel(Channel channel) {
        // 유저 유효성 검사
        if (userStatus != UserType.UserStatus.ACTIVE) {
            return;
        }
        // 채널 유효성 검사
        if (channel.getId() == null || channels.contains(channel)) {
            System.out.println("<실패: 없는 채널이거나 이미 가입되어 있습니다.>");
        }
        channels.add(channel);
    }

    // 채널 나가기
    public void outFromChannel(Channel channel) {
        if (!channels.contains(channel)) {
            System.out.println("<실패: 가입되어 있지 않은 채널을 나갈 수는 없습니다.>");
            return;
        }
        channels.remove(channel);
        channel.removeMessagesByUserIdFromActiveChannel(super.getId());
        channel.removeUserByUserIdFromActiveChannel(super.getId());
        removeMessageByChannelId(channel.getId());

    }

    // 메시지 서비스에서 생성된 메시지를 리스트에 추가함
    public void addMessage(Message newMessage) {
        Optional<Message> nullableMessage = Optional.ofNullable(newMessage);
        nullableMessage.ifPresent(message -> {
            if (messages.contains(message)) {
                System.out.println("<실패: 보낸 메시지입니다.>");
            } else {
                messages.add(newMessage);
            }
        });
    }

    // 메시지 아이디로 메시지 삭제
    public void removeMessageByMessageId(UUID messageId) {
        messages.removeIf(m -> messageId.equals(m.getId()));
    }

    // 동시성 코드: 채널 관련 정보 삭제
    // 메시지 목록에서 특정 채널에 보낸 메시지만 삭제(재설정)
    public void removeMessageByChannelId(UUID channelId) {
        List<Message> removedMessages = messages.stream()
                .filter(message -> message.getChannel().getId() != channelId)
                .collect(Collectors.toList());
        setMessages(removedMessages);
    }

    // 채널 목록에서 특정 채널만 삭제(재설정)
    public void removeChannelByChannelId(UUID channelId) {
        List<Channel> removedChannels = channels.stream()
                .filter(channel -> channel.getId() != channelId)
                .collect(Collectors.toList());
        setChannels(removedChannels);
    }

    // 유저 삭제 시 실행: 채널의 유저 정보, 유저 정보 삭제
    public void removeAllChannelsAndMessages() {
        for (Channel channel : channels) {
            // 채널에서 유저 정보 삭제(유저, 메시지)
            channel.removeMessagesByUserIdFromActiveChannel(super.getId());
            channel.removeUserByUserIdFromActiveChannel(super.getId());
        }
        // 채널, 메시지 비우기
        channels.clear();
        messages.clear();
        setUpdatedAt(System.currentTimeMillis());
        setStatus(UserType.UserStatus.QUIT);
    }
}
