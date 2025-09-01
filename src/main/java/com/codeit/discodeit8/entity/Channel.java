package com.codeit.discodeit8.entity;


import com.codeit.discodeit8.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel extends BaseUpdatableEntity {

  @Column(name = "type", nullable = false)
  private ChannelType type;

  @Column(name = "name", nullable = true)
  private String name;

  @Column(name = "description", nullable = true)
  private String description;
}