
package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {
    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany
    @JoinTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message(String content, Channel channel, User author) {
        super();
        this.content = content;
        this.channel = channel;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + getId() +
                ", messageCreatedAt=" + getCreatedAt() +
                ", messageUpdatedAt=" + getUpdatedAt() +
                ", newContent='" + content +
                "', userId=" + author.getId() +
                ", ChannelId=" + channel.getId() +
                '}';
    }

    public UUID getAuthorId() {
        return author.getId();
    }

    public UUID getChannelId() {
        return channel.getId();
    }


    public void setContent(String content) {
        this.content = content;
    }
}
