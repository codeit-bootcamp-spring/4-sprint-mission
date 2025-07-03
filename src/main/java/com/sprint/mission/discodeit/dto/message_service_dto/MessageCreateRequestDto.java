package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class MessageCreateRequestDto {
    private UserResponseDto userResponseDto;
    private String channelName;
    private String messageContents;
    private List<MultipartFile> extraContentsFiles;

    public MessageCreateRequestDto(UserResponseDto userResponseDto, String channelName, String messageContents, List<MultipartFile> extraContentsFiles) {
        this.userResponseDto = userResponseDto;
        this.channelName = channelName;
        this.messageContents = messageContents;
        this.extraContentsFiles = extraContentsFiles;
    }
}
