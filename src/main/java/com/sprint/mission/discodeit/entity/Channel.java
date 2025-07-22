package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Channel extends BaseUpdateEntity{

  private static final long serialVersionUID = 1L;
  //private UUID id;
  //private Instant createdAt;
  //private Instant updatedAt;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChannelType type;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  public Channel(ChannelType type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
//    boolean anyValueUpdated = false;
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
//      anyValueUpdated = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
//      anyValueUpdated = true;
    }

//    if (anyValueUpdated) {
//      this.updatedAt = Instant.now();
//    }
  }
}
