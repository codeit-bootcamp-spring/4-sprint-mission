package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponseDto(
        UUID id, String userName, String userEmail, boolean status, byte[] image) {}
