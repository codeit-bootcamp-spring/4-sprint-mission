package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.FileType;

import java.util.UUID;

public record BinaryContentCreateDto(
    UUID userId,
    UUID messageId,
    BinaryContentType binaryContentType,
    byte[] bytes,
    FileType fileType,
    String fileName,
    Long fileSize
) {}
