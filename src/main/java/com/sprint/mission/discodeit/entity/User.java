package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class User extends BaseEntity {

    // enum
    // 활성, 휴면, 정지, 탈퇴
    public enum Status {
        ACTIVE, SLEEP, BANNED, QUIT
    }

    private String userName;
    private Status status;

    private List<Channel> channels = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public User(String userName) {
        super();
        this.userName = userName;
        this.status = Status.ACTIVE;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + id +
                ", userName='" + userName + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}' + "\n";
    }

    // 채널 관련 메서드
    // 매개변수로 받은 channel 인수 추가
    public void addChannel(Channel channel) {
        // 유저 유효성 검사
        if (status != Status.ACTIVE) {
            return;
        }
        // 채널 유효성 검사
        if (channel.getId() == null) {
            System.out.println("<실패: 없는 채널입니다.>");
        } else if (channels.contains(channel)) {
            System.out.println("<실패: 이미 가입되어 있는 채널입니다.>");
        } else {
            channels.add(channel);
        }
    }

    // 채널 나가기
    public void outFromChannel(Channel channel) {
        if (!channels.contains(channel)) {
            System.out.println("<실패: 가입되어 있지 않은 채널을 나갈 수는 없습니다.>");
            return;
        }
        channels.remove(channel);
        channel.removeMessagesByUserId(id);
        channel.removeUserByUserId(id);
        removeMessageByChannelId(channel.getId());

    }

    // 메시지 서비스에서 생성된 메시지를 리스트에 추가함
    public void addMessage(Message message) {
        Optional<Message> nullableMessage = Optional.ofNullable(message);
        nullableMessage.ifPresent(m -> {
            if (messages.contains(m)) {
                System.out.println("<실패: 보낸 메시지입니다.>");
            } else {
                messages.add(message);
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
                .filter(m -> m.getChannel().getId() != channelId)
                .collect(Collectors.toList());
        setMessages(removedMessages);
    }

    // 채널 목록에서 특정 채널만 삭제(재설정)
    public void removeChannelByChannelId(UUID channelId) {
        List<Channel> removedChannels = channels.stream()
                .filter(c -> c.getId() != channelId)
                .collect(Collectors.toList());
        setChannels(removedChannels);
    }

    // 유저 삭제 시 실행: 채널의 유저 정보, 유저 정보 삭제
    public void removeAllChannelsAndMessages() {
        for (Channel channel : channels) {
            // 채널에서 유저 정보 삭제(유저, 메시지)
            channel.removeMessagesByUserId(id);
            channel.removeUserByUserId(id);
        }
        // 채널, 메시지 비우기
        channels.clear();
        messages.clear();
    }
}
