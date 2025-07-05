package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentResponseDto(
        UUID id, UUID userId, UUID messageId, String data, String dataType) {}
