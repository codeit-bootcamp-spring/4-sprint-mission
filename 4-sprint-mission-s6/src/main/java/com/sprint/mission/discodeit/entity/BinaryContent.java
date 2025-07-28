package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "binary_content")
public class BinaryContent extends BaseEntity {

  //
  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "bytes", nullable = false)
  private byte[] bytes;
/*

  public BinaryContent(String fileName, Long size, String contentType) {
    //
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
*/

  public BinaryContent(UUID contentId, String fileName, Long size, String contentType) {
    super(contentId);
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  public void updateMeta(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
}
