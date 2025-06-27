package com.sprint.mission.discodeit.dto.binary_contents_dto;

import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindBinaryContentRequestDto {
    private final UUID referenceId;

    public FindBinaryContentRequestDto(UserResponseDto userResponseDto) {
        this.referenceId = userResponseDto.getUserId();
    }
    public FindBinaryContentRequestDto(MessageResponseDto messageResponseDTO) {
        this.referenceId = messageResponseDTO.getMessageId();
    }
}
