package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentCreateDto(UUID userId, UUID messageId, byte[] data, String dataType) {}
