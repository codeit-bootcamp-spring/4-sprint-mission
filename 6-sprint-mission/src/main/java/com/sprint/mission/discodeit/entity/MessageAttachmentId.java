package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MessageAttachmentId implements Serializable {

  private UUID message;
  private UUID attachment;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MessageAttachmentId that)) return false;
    return Objects.equals(message, that.message) &&
        Objects.equals(attachment, that.attachment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, attachment);
  }
}
