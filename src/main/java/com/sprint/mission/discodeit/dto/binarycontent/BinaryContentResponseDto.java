package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.FileType;

import java.util.UUID;

public record BinaryContentResponseDto(
    UUID id,
    byte[] bytes,
    FileType fileType,
    String fileName,
    Long fileSize
) {}
