package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

@Getter
public class ChannelNameUpdateRequestDto {
    private final UserResponseDto userResponseDto;
    private final ChannelResponseDto channelResponseDto;
    private final  String channelNewName;

    public ChannelNameUpdateRequestDto(UserResponseDto userResponseDto, ChannelResponseDto channelResponseDto, String channelNewName) {
        this.userResponseDto = userResponseDto;
        this.channelResponseDto = channelResponseDto;
        this.channelNewName = channelNewName;
    }
}
