package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BinaryContentEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final UUID userId; // api-docs.json 스키마에 없지만 기존 코드에 있으므로 유지
  private final UUID messageId; // api-docs.json 스키마에 없지만 기존 코드에 있으므로 유지
  private final Instant createdAt; // api-docs.json 스키마에 있지만 기존 코드에 있으므로 유지

  private final String fileName; // api-docs.json 스키마에 추가된 필드
  private final long size; // api-docs.json 스키마에 추가된 필드
  private final String contentType; // api-docs.json 스키마에 추가된 필드
  @Setter
  private byte[] data; // api-docs.json 스키마의 'bytes' 필드에 해당, byte[]로 유지

  public BinaryContentEntity(
      UUID userId,
      UUID messageId,
      String fileName,
      long size,
      String contentType,
      byte[] data) {
    this.userId = userId;
    this.messageId = messageId;
    this.createdAt = Instant.now();
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.data = data;
    this.id = UUID.randomUUID();
  }
}
