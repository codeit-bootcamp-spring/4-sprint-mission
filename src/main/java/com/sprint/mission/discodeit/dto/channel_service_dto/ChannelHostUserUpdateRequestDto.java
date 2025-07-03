package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

@Getter
public class ChannelHostUserUpdateRequestDto {
    private final UserResponseDto oldHostUserResponseDto;
    private final UserResponseDto newHostUserResponseDto;
    private final ChannelResponseDto channelResponseDto;

    public ChannelHostUserUpdateRequestDto(UserResponseDto oldHostUserResponseDto, UserResponseDto newHostUserResponseDto, ChannelResponseDto channelResponseDto) {
        this.oldHostUserResponseDto = oldHostUserResponseDto;
        this.newHostUserResponseDto = newHostUserResponseDto;
        this.channelResponseDto = channelResponseDto;
    }
}
