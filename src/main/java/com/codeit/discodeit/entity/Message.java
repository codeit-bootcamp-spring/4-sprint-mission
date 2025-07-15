package com.codeit.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class Message extends BaseEntity implements Serializable {

  private String content;
  private UUID authorId;
  private UUID channelId;

  private List<UUID> attachmentIds = new ArrayList<>();


  public Message(User user, Channel channel, String contents) {
    super();
    this.content = contents;
    this.authorId = user.getId();
    this.channelId = channel.getId();
  }

  public void updateMessageContent(String messageContents) {
    updateUpdatedAt();
    this.content = messageContents;
  }

  public void registerMessageToUserAndChannel(User user, Channel channel) {
    user.addMessage(this);
    channel.addMessage(this);
  }

  public void addBinaryContentId(UUID binaryContentsId) {
    attachmentIds.add(binaryContentsId);
  }

  public void clearBinaryContentId() {
    attachmentIds.clear();
  }
}
