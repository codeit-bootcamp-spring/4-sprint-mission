package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "read_statuses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "channel_id"})
        }
)
public class ReadStatus extends BaseUpdatableEntity {
    @Column(nullable = false)
    private Instant lastReadAt;

    @Column(nullable = false)
    private boolean notificationEnabled;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    public ReadStatus(User user, Channel channel) {
        this(user, channel, Instant.now());
    }

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
        this.notificationEnabled = channel.getType().equals(ChannelType.PUBLIC);
    }

    private ReadStatus(UUID id, User user, Channel channel) {
        this(user, channel);
        setId(id);
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "userId=" + user.getId() +
                ", channelId=" + channel.getId() +
                ", newLastReadAt=" + lastReadAt +
                '}';
    }

    public UUID getUserId() {
        return user.getId();
    }

    public UUID getChannelId() {
        return channel.getId();
    }

    public static ReadStatus of(UUID id, User user, Channel channel) {
        return new ReadStatus(id, user, channel);
    }
}
