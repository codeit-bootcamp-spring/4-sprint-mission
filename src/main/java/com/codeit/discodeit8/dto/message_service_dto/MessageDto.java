package com.codeit.discodeit8.dto.message_service_dto;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit8.dto.user_service_dto.UserDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {

}