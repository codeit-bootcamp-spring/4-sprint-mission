package com.codeit.discodeit8.dto.user_status_dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}