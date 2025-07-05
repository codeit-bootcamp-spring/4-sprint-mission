package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<UUID> userIds = new ArrayList<>();
    private List<UUID> messageIds = new ArrayList<>();
    @Setter private String channelName;
    private ChannelState state;
    private ChannelType type;
    @Setter private String description;

    public Channel(String channelName, String description, ChannelType type) {
        this.channelName = channelName;
        this.description = description;
        this.type = type;
        state = ChannelState.ACTIVATED;
    }

    // 양방향 연결
    public void addUser(User user) {
        if (userIds.contains(user.getId())) {
            throw new IllegalArgumentException("해당 유저는 이미 채널에 존재합니다.");
        }
        userIds.add(user.getId());
        user.getChannelIds().add(this.getId());
    }

    public void removeUser(User user) {
        if (!userIds.contains(user.getId())) {
            throw new IllegalArgumentException("해당 유저는 채널에 존재하지 않습니다.");
        }
        userIds.remove(user.getId());
        user.getChannelIds().remove(this.getId());
    }

    public void addMessage(Message message) {
        if (messageIds.contains(message.getId())) {
            throw new IllegalArgumentException("해당 메시지는 이미 채널에 존재합니다.");
        }
        messageIds.add(message.getId());
        message.addChannel(this);
    }

    public void removeMessage(Message message) {
        if (!messageIds.contains(message.getId())) {
            throw new IllegalArgumentException("해당 메시지는 채널에 존재하지 않습니다.");
        }
        messageIds.remove(message.getId());
        message.removeChannel(this);
    }

    // 상태
    public void setToActivate() {
        state = ChannelState.ACTIVATED;
    }

    public void setToDeactivate() {
        state = ChannelState.DEACTIVATED;
    }

    public void setToDelete() {
        state = ChannelState.DELETED;
    }

    @Override
    public String toString() {
        String userIdString = userIds.stream().map(UUID::toString).collect(Collectors.joining(", "));

        return "Channel{"
                + "\n"
                + "channelName="
                + channelName
                + "\n"
                + "channelsUsers="
                + userIdString
                + "\n"
                + "createdAt="
                + getCreatedAt()
                + "\n"
                + "updatedAt="
                + getUpdatedAt()
                + "\n"
                + '}'
                + "\n";
    }
}
