package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

@Getter
public class DeleteMessageRequestDto {
    UserResponseDto userResponseDto;
    MessageResponseDto messageResponseDTO;

    public DeleteMessageRequestDto(UserResponseDto userResponseDto, MessageResponseDto messageResponseDTO) {
        this.userResponseDto = userResponseDto;
        this.messageResponseDTO = messageResponseDTO;
    }
}
