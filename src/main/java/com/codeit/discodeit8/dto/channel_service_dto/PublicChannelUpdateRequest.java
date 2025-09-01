package com.codeit.discodeit8.dto.channel_service_dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicChannelUpdateRequest {

  @NotEmpty
  private String newName;

  @NotEmpty
  private String newDescription;
}
