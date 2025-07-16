package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ReadStatusEntity extends BaseEntity {

  private UUID userId;
  private UUID channelId;
  private Instant lastReadAt;

  public ReadStatusEntity(UUID userId, UUID channelId) {
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = Instant.now();
  }

  public List<MessageEntity> hasUnreadMessages(List<MessageEntity> messageEntities) {
    return messageEntities.stream()
        .filter(message -> message.getCreatedAt().isAfter(lastReadAt))
        .collect(Collectors.toList());
  }

  public void updateReadTime(Instant newLastReadAt) {
    this.lastReadAt = newLastReadAt;
    setUpdatedAt();
  }
}
