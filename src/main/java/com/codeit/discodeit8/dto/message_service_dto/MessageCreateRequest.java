package com.codeit.discodeit8.dto.message_service_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreateRequest {

  @NotEmpty
  private String content;

  @NotNull
  private UUID channelId;

  @NotNull
  private UUID authorId;
}
