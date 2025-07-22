package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class Message extends BaseUpdateEntity {

  private static final long serialVersionUID = 1L;

//  private UUID id;
//  private Instant createdAt;
//  private Instant updatedAt;
  private String content;
  //private UUID channelId;
  private User author;
  private List<BinaryContent> attachments;
  private Channel channel;

  public Message(String content, Channel channel, User authorId, List<BinaryContent> attachments) {
    //
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments;
  }

  public void update(String newContent) {
//    boolean anyValueUpdated = false;
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
//      anyValueUpdated = true;
    }

//    if (anyValueUpdated) {
//      this.updatedAt = Instant.now();
//    }
  }
}
