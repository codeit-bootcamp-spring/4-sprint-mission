package com.codeit.discodeit.dto.channel_service_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicChannelUpdateRequest {

  private String newName;
  private String newDescription;

  public PublicChannelUpdateRequest(String newName, String newDescription) {
    this.newName = newName;
    this.newDescription = newDescription;
  }

  public PublicChannelUpdateRequest() {
  }
}
