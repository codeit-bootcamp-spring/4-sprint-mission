package com.codeit.discodeit8.dto.channel_service_dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChannelCreateRequest {

  @NotNull
  public List<UUID> participantIds;
}
