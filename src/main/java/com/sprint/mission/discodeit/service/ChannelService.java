package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponseDto createPublicChannel(CreateChannelRequestDto createChannelRequestDTO);
    ChannelResponseDto createPrivateChannel(CreateChannelRequestDto createChannelRequestDTO, UserResponseDto enterUserResponseDto);

    void deleteChannel(DeleteChannelRequestDto deleteChannelRequestDTO);

    void addUserToChannel(AddUserToChannelRequestDto addUserToChannelRequestDTO);
    void leaveUserFromChannel(LeaveUserFromChannelRequestDto leaveUserFromChannelRequestDTO);

    void updateChannelName(ChannelNameUpdateRequestDto channelNameUpdateRequestDTO);
    void updateHostUser(ChannelHostUserUpdateRequestDto channelHostUserUpdateRequestDTO);

    List<ChannelResponseDto> findPublicChannel();
    List<ChannelResponseDto> findPrivateChannel(UserResponseDto user);

    ChannelResponseDto findChannelDTOByCannelId(UUID chanelId);

    List<ReadStatus> findAllReadStatus();
}
