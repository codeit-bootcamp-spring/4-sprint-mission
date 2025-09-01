package com.codeit.discodeit8.entity;

import com.codeit.discodeit8.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "binaryContents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinaryContent extends BaseEntity {

  @Column(name = "fileName", nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private long size;

  @Column(name = "contentType", nullable = false)
  private String contentType;

}