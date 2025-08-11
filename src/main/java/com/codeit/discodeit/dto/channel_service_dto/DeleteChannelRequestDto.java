package com.codeit.discodeit.dto.channel_service_dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class DeleteChannelRequestDto {

  public UUID userId;
  public UUID channelId;

  public DeleteChannelRequestDto(UUID userId, UUID channelId) {
    this.userId = userId;
    this.channelId = channelId;
  }
}
