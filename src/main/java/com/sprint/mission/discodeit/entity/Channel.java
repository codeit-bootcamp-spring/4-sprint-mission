package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Channel extends BasedEntity {
    private String channelName;
    private ChannelType type;
    private final List<UUID> userIds = new ArrayList<>();
    private final List<UUID> messageIds = new ArrayList<>();
    private String description;

    public Channel(String name, String description) {
        this.channelName = name;
        this.description = description;
        this.type = ChannelType.PUBLIC;
    }

    public Channel(List<UUID> userIds) {
        this.type = ChannelType.PRIVATE;
    }

    @Override
    public String toString() {
        if (type == ChannelType.PUBLIC) {
            return "Channel{" +
                    "ChannelType=" + type +
                    ", channelId=" + super.getId() +
                    ", channelCreatedAt=" + super.getCreatedAt() +
                    ", channelUpdatedAt=" + super.getUpdatedAt() +
                    ", channelName='" + channelName + '\'' +
                    '}';
        } else if (type == ChannelType.PRIVATE) {
            return "Channel{" +
                    "ChannelType=" + type +
                    ", channelCreateAt=" + super.getCreatedAt() +
                    ", channelUsers=" + getUserIds() +
                    "}";
        }
        return "Invalid type provided";
    }

    public void addMessageToChannel(Message message) {
        if (this.messageIds.contains(message.getId())) throw new IllegalArgumentException("Message already exists");
        this.messageIds.add(message.getId());
        message.addChannel(this);
    }

    public void removeMessageFromChannel(Message message) {
        if (!this.messageIds.contains(message.getId())) throw new IllegalArgumentException("Message already removed");
        this.messageIds.remove(message.getId());
        message.removeChannel();
    }

    public void addUser(UUID userId) {
        if (this.userIds.contains(userId)) throw new IllegalArgumentException("User already exists");
        this.userIds.add(userId);
    }

    public void clearMessages() {
        this.messageIds.clear();
    }
}
