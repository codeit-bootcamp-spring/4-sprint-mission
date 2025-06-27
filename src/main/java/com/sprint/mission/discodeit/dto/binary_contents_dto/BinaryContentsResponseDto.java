package com.sprint.mission.discodeit.dto.binary_contents_dto;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.BinaryContents;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentsResponseDto {
    private UUID binaryContentsId;
    private UUID referenceId;
    private final BinaryContentType binaryContentType;
    private final byte[] binaryData;

    public BinaryContentsResponseDto(BinaryContents binaryContents) {
        this.binaryContentsId = binaryContents.getId();
        this.binaryContentType = binaryContents.getBinaryContentType();
        this.binaryData = binaryContents.getBinaryData();
        this.referenceId = binaryContents.getReferenceId();
    }

}
