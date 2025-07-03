package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class MessageUpdateRequestDto {
    UserResponseDto userResponseDto;
    UUID messageId;
    String newContent;
    private List<MultipartFile> extraContentsFiles;

    public MessageUpdateRequestDto(UserResponseDto userResponseDto, UUID messageId, String newContent, List<MultipartFile> extraContentsFiles) {
        this.userResponseDto = userResponseDto;
        this.messageId = messageId;
        this.newContent = newContent;
        this.extraContentsFiles = extraContentsFiles;
    }
}
