package com.codeit.discodeit8.dto.user_service_dto;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online
) {

}