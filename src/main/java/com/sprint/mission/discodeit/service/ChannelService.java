package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // CRUD
    ChannelDto createPublic(PublicChannelRequest request);
    ChannelDto createPrivate(PrivateChannelRequest request);
    ChannelDto findChannel(UUID channelId);
    List<ChannelDto> findAllChannels(UUID userId);
    ChannelDto updatePublicChannel(UUID channelId,PublicChannelRequest request);
    void deleteChannel(UUID channelId);
}
