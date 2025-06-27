package com.sprint.mission.discodeit.dto.binary_contents_dto;

import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CreateBinaryContentsRequestDto {

    private final UUID referenceId;
    private final String filePath;
    private final BinaryContentType binaryContentType;

    public CreateBinaryContentsRequestDto(UUID referenceId, String filePath, BinaryContentType binaryContentType) {
        this.referenceId = referenceId;
        this.filePath = filePath;
        this.binaryContentType = binaryContentType;
    }

}