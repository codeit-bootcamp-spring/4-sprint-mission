package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MessageEntity extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  private UUID channelId;
  private UUID authorId;
  @Setter
  private List<UUID> attachmentIds = new ArrayList<>();
  @Setter
  private String contents;
  private MessageState state;

  public MessageEntity(String messageContents, UserEntity userEntity, ChannelEntity channelEntity) {
    this.contents = messageContents;
    addChannel(channelEntity);
    addUser(userEntity);
  }

  public void addChannel(ChannelEntity channelEntity) {
    if (this.channelId != null) {
      return;
    }
    this.channelId = channelEntity.getId();
    channelEntity.addMessage(this);
  }

  public void removeChannel(ChannelEntity channelEntity) {
    if (channelId == null) {
      return;
    }
    channelEntity.removeMessage(this);
  }

  public void addUser(UserEntity userEntity) {
    if (authorId != null) {
      return;
    }
    authorId = userEntity.getId();
    userEntity.addMessage(this);
  }

  public void removeUser(UserEntity userEntity) {
    if (authorId == null) {
      return;
    }
    userEntity.removeMessage(this);
  }

  // 상태
  public void setToVisible() {
    state = MessageState.VISIBLE;
  }

  public void setToInvisible() {
    state = MessageState.INVISIBLE;
  }

  public void setToDeleted() {
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
