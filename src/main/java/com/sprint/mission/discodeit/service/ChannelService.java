package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelPublicCreateResponseDto createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest);

    ChannelPrivateCreateResponseDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelResponseDto findByChannelId(UUID channelId);

    ChannelResponseDto updateChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);

    void deleteChannel(UUID channelId);

    List<ChannelResponseDto> findAllChannels();
}
