package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Not;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDto {
  private UUID id;
  private Instant createdAt;
  private UUID receiverId;
  private String title;
  private String content;

  public NotificationDto(Notification notification) {
    this.id = notification.getId();
    this.createdAt = notification.getCreatedAt();
    this.receiverId = notification.getReceiverId();
    this.title = notification.getTitle();
    this.content = notification.getContent();
  }

  public NotificationDto(UUID id, Instant createdAt, UUID receiverId, String title, String content) {
    this.id = id;
    this.createdAt = createdAt;
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }
}