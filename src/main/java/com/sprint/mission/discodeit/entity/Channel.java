package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "channels")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Channel extends BaseUpdatableEntity {

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private ChannelType type;

  @Column(length = 100, nullable = true)
  private String name;

  @Column(length = 500, nullable = true)
  private String description;
}
