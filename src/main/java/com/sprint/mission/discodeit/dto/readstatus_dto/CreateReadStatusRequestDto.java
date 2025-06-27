package com.sprint.mission.discodeit.dto.readstatus_dto;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReadStatusRequestDto {
    private final UserResponseDto userResponseDto;
    private final ChannelResponseDto channelResponseDto;
    private UUID messageId;

    public CreateReadStatusRequestDto(UserResponseDto userResponseDto, ChannelResponseDto channelResponseDto) {
        this.userResponseDto = userResponseDto;
        this.channelResponseDto = channelResponseDto;
        this.messageId = channelResponseDto.getLastMessageId();
    }
}
