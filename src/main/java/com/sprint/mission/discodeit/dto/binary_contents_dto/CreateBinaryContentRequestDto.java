package com.sprint.mission.discodeit.dto.binary_contents_dto;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBinaryContentRequestDto {

    private final UUID referenceId;
    private final String filePath;
    private final BinaryContentType binaryContentType;

    public CreateBinaryContentRequestDto(UUID referenceId, String filePath, BinaryContentType binaryContentType) {
        this.referenceId = referenceId;
        this.filePath = filePath;
        this.binaryContentType = binaryContentType;
    }
}