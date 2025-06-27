package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageCreateRequestDto {
    private final UserResponseDto userResponseDto;
    private final ChannelResponseDto channelResponseDto;
    private final String messageContents;
    private final List<String> extraContentsFilePath;

    public MessageCreateRequestDto(UserResponseDto userResponseDto, ChannelResponseDto channelResponseDto, String messageContents, List<String> extraContentsFilePath) {
        this.userResponseDto = userResponseDto;
        this.channelResponseDto = channelResponseDto;
        this.messageContents = messageContents;
        this.extraContentsFilePath = extraContentsFilePath;
    }

    public MessageCreateRequestDto(UserResponseDto userResponseDto, ChannelResponseDto channelResponseDto, String messageContents) {
        this.userResponseDto = userResponseDto;
        this.channelResponseDto = channelResponseDto;
        this.messageContents = messageContents;
        this.extraContentsFilePath = null;
    }
}
