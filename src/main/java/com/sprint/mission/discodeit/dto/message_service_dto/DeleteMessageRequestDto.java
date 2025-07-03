package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class DeleteMessageRequestDto {
    UserResponseDto userResponseDto;
    UUID messageId;

    public DeleteMessageRequestDto(UserResponseDto userResponseDto, UUID messageId) {
        this.userResponseDto = userResponseDto;
        this.messageId = messageId;
    }
}
