package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateDto(UUID id, String name, byte[] image) {}
