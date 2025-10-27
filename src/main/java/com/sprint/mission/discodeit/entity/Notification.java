package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
  @Column(name = "title")
  private String title;

  @Column(name = "content")
  private String content;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public Notification(String title, String content, User user) {
    this.title = title;
    this.content = content;
    this.user = user;
  }
}
