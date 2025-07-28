package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "binary_contents")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BinaryContent extends BaseEntity {

  @Column(length = 255, nullable = false)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(length = 100, nullable = false)
  private String contentType;

  @Column(nullable = false)
  private byte[] bytes;
}
