package com.codeit.discodeit.dto.message_service_dto;

import com.codeit.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class MessageUpdateRequest {

  String newContent;

  public MessageUpdateRequest(String newContent) {
    this.newContent = newContent;
  }

  public MessageUpdateRequest() {
  }
}
