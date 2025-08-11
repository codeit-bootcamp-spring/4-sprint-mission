package com.codeit.discodeit.dto.readstatus_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Message 읽음 상태 생성 정보")
@Getter
@Setter
public class ReadStatusCreateRequest {

  @NotNull
  UUID userId;

  @NotNull
  UUID channelId;

  Instant lastReadAt;

  public ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = Instant.now();
  }

  public ReadStatusCreateRequest() {
  }


  public void setLatestReadAt() {
    this.lastReadAt = Instant.now();
  }
}
