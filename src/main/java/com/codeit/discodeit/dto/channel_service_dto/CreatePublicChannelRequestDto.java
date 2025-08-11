package com.codeit.discodeit.dto.channel_service_dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePublicChannelRequestDto {

  String name;
  String description;

  public CreatePublicChannelRequestDto() {
    // 기본 생성자
  }

  public CreatePublicChannelRequestDto(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
