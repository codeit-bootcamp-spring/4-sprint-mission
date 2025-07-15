package com.codeit.discodeit.dto.channel_service_dto;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreatePrivateChannelRequestDto {

  public ArrayList<UUID> participantIds;

  public CreatePrivateChannelRequestDto() {
  }

  public CreatePrivateChannelRequestDto(ArrayList<UUID> participantIds) {
    this.participantIds = participantIds;
  }
}
