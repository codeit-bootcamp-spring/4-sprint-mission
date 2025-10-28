package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseUpdatableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", columnDefinition = "uuid")
    private User receiver;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", columnDefinition = "uuid")
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid")
    private Channel channel;
    @Column(columnDefinition = "text", nullable = false)
    private String content;

    public Notification(User receiver, User sender, Channel channel, String content) {
        this.receiver = receiver;
        this.sender = sender;
        this.channel = channel;
        this.content = content;
    }
}
