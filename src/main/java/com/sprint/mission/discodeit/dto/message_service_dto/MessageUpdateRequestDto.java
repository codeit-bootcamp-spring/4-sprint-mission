package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageUpdateRequestDto {
    UserResponseDto userResponseDto;
    MessageResponseDto messageResponseDTO;
    String newContent;
    List<String> extraContentsFilePath;

    public MessageUpdateRequestDto(UserResponseDto userResponseDto, MessageResponseDto messageResponseDTO, String newContent, List<String> extraContentsFilePath) {
        this.userResponseDto = userResponseDto;
        this.messageResponseDTO = messageResponseDTO;
        this.newContent = newContent;
        this.extraContentsFilePath = extraContentsFilePath;
    }
}
