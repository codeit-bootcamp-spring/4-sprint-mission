package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto createPublicChannel(PublicChannelCreateDto dto);
    ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto);
    ChannelResponseDto findById(UUID channelId, UUID userId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    ChannelResponseDto update(ChannelUpdateDto dto);
    void delete(UUID channelId);
}
