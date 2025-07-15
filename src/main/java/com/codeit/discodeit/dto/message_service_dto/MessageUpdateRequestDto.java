package com.codeit.discodeit.dto.message_service_dto;

import com.codeit.discodeit.entity.User;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class MessageUpdateRequestDto {

  User user;
  UUID messageId;
  String newContent;
  private List<MultipartFile> extraContentsFiles = new ArrayList<>();

  public MessageUpdateRequestDto(User user, UUID messageId, String newContent,
      List<MultipartFile> extraContentsFiles) {
    this.user = user;
    this.messageId = messageId;
    this.newContent = newContent;
    this.extraContentsFiles = extraContentsFiles;
  }

  public MessageUpdateRequestDto() {
  }
}
