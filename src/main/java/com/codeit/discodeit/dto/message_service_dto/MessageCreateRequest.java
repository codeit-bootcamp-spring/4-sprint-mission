package com.codeit.discodeit.dto.message_service_dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MessageCreateRequest {

  private String content;
  private UUID channelId;
  private UUID authorId;

  public MessageCreateRequest() {
  }
}
