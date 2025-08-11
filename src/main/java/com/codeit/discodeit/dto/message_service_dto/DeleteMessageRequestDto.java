package com.codeit.discodeit.dto.message_service_dto;

import com.codeit.discodeit.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class DeleteMessageRequestDto {

  User user;
  UUID messageId;

  public DeleteMessageRequestDto(User user, UUID messageId) {
    this.user = user;
    this.messageId = messageId;
  }

  public DeleteMessageRequestDto() {
  }
}
