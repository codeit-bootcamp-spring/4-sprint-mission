package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponseDto createPublicChannel(CreatePublicChannelRequestDto createPublicChannelRequestDTO);
    ChannelResponseDto createPrivateChannel(CreatePrivateChannelRequestDto createPrivateChannelRequestDto);

    void deleteChannel(DeleteChannelRequestDto deleteChannelRequestDTO);

    void addUserToChannel(AddUserToChannelRequestDto addUserToChannelRequestDTO);
    void leaveUserFromChannel(LeaveUserFromChannelRequestDto leaveUserFromChannelRequestDTO);

    ChannelResponseDto updateChannelName(ChannelNameUpdateRequestDto channelNameUpdateRequestDTO);
    void updateHostUser(ChannelHostUserUpdateRequestDto channelHostUserUpdateRequestDTO);

    List<ChannelResponseDto> findPublicChannels();
    List<ChannelResponseDto> findPrivateChannelsByUserId(UserResponseDto user);
    List<ChannelResponseDto> findAllChannelByUserId(UUID userId);

    ChannelResponseDto findChannelDtoByChannelId(UUID chanelId);
    ChannelResponseDto findChannelDtoByChannelName(String channelName);

    void enterChannel(UUID userId, UUID channelId);

}
