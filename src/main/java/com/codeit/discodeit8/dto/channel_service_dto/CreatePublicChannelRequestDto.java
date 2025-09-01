package com.codeit.discodeit8.dto.channel_service_dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePublicChannelRequestDto {

  @NotEmpty
  String name;

  @NotEmpty
  String description;
}
