package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds = new ArrayList<>();
    private String contents;
    private MessageState state;

    public Message(String messageContents, UUID authorId, UUID channelId) {
        this.contents = messageContents;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setAttachmentIds(List<UUID> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public void addChannel(Channel channel) {
        if (channelId != null) {
            throw new IllegalArgumentException("이미 채널에 작성된 메세지입니다.");
        }
        channelId = channel.getId();
        channel.addMessage(this);
    }

    public void removeChannel(Channel channel) {
        if (channelId == null) {
            throw new IllegalArgumentException("이미 삭제된 메세지입니다.");
        }
        channel.removeMessage(this);
    }

    public void addUser(User user) {
        if (authorId != null) {
            throw new IllegalStateException("이미 작성자가 지정된 메시지입니다.");
        }
        authorId = user.getId();
        user.addMessage(this);
    }

    public void removeUser(User user) {
        if (authorId == null) {
            throw new IllegalStateException("이미 삭제된 메시지입니다.");
        }
        user.removeMessage(this);
    }

    // 상태
    public void visibleMessageState() {
        state = MessageState.VISIBLE;
    }

    public void invisibleMessageState() {
        state = MessageState.INVISIBLE;
    }

    public void deletedMessageState() {
        state = MessageState.DELETED;
    }

    @Override
    public String toString() {

        return "Message{"
                + "\n"
                + "messageContents="
                + contents
                + "\n"
                + "userId="
                + authorId
                + "\n"
                + "channelId="
                + channelId
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
