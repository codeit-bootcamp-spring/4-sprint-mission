package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {
    @Column
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;
    @Column
    private String description;

    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        this.type = ChannelType.PUBLIC;
    }

    public Channel() {
        super();
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
                    ", channelName='" + name + '\'' +
                    '}';
        } else if (type == ChannelType.PRIVATE) {
            return "Channel{" +
                    "ChannelType=" + type +
                    ", channelCreateAt=" + super.getCreatedAt() +
                    "}";
        }
        return "Invalid type provided";
    }
}
