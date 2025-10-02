package com.sprint.mission.discodeit.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "message", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MessageAttachment> attachments = new ArrayList<>();

    @Builder
    public Message(String content, Channel channel, User author) {
        this.content = content;
        this.channel = channel;
        this.author = author;
    }

    public UUID getAuthorId() {
        return author.getId();
    }

    public UUID getChannelId() {
        return channel.getId();
    }

    public void editContent(String content) {
        this.content = content;
    }

    public void addAttachments(List<MessageAttachment> attachments) {
        this.attachments.addAll(attachments);
    }

    public void removeAttachments(List<MessageAttachment> attachments) {
        this.attachments.removeAll(attachments);
    }

    public void addAttachment(MessageAttachment attachment) {
        this.attachments.add(attachment);
    }

    public void removeAttachment(MessageAttachment attachment) {
        this.attachments.remove(attachment);
    }
}
