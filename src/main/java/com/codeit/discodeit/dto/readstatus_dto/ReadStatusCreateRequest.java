package com.codeit.discodeit.dto.readstatus_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Message 읽음 상태 생성 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusCreateRequest {

  @NotBlank
  UUID userId;
  @NotBlank
  UUID channelId;
  Instant lastReadAt;
}