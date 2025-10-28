package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification extends BaseEntity {
  @Column(nullable = false)
  private UUID receiverId;  // 알림 받는 사용자

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 500)
  private String content;
}
