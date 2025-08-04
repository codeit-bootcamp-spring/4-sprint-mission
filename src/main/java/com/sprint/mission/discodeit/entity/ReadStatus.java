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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable= false)
    private Channel channel;

    public ReadStatus(User user, Channel channel) {
        super();
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        super();
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
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
//    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
//        super();
//        this.userId = userId;
//        this.channelId = channelId;
//        this.lastReadAt = lastReadAt;
//    }
}
