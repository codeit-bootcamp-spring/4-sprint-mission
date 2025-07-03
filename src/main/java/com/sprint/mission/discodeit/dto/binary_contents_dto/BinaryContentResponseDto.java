package com.sprint.mission.discodeit.dto.binary_contents_dto;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentResponseDto {
    private UUID binaryContentsId;
    private UUID referenceId;
    private final BinaryContentType binaryContentType;
    private final byte[] binaryData;

    public BinaryContentResponseDto(BinaryContent binaryContent) {
        this.binaryContentsId = binaryContent.getId();
        this.binaryContentType = binaryContent.getBinaryContentType();
        this.binaryData = binaryContent.getBytes();
        this.referenceId = binaryContent.getReferenceId();
    }

}
