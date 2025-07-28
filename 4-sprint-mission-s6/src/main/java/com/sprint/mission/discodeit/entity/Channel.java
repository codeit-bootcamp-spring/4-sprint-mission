package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "channel")
public class Channel extends BaseUpdatableEntity {

  //
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private ChannelType type;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description")
  private String description;

  public Channel(ChannelType type, String name, String description) {
    //
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
  }
}
