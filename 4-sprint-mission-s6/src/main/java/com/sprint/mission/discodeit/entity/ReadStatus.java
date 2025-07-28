package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "read_statuses", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
public class ReadStatus extends BaseUpdatableEntity {
  //
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    //
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant newLastReadAt) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
    }
  }
}
