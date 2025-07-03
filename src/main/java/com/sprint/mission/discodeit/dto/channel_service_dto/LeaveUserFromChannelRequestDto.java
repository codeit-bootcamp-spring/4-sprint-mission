package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;

@Getter
public class LeaveUserFromChannelRequestDto {
    UserResponseDto userResponseDto;
    ChannelResponseDto channelResponseDto;

    public LeaveUserFromChannelRequestDto(UserResponseDto userResponseDto, ChannelResponseDto channelResponseDto) {
        this.userResponseDto = userResponseDto;
        this.channelResponseDto = channelResponseDto;
    }
}
