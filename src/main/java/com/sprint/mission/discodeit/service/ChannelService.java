package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelDto.createPublicChannel request);

    Channel create(ChannelDto.createPrivateChannel request);

    ChannelDto.response find(UUID channelId);

    List<ChannelDto.response> findAllByUserId(UUID userId);

    Channel update(UUID channelId, ChannelDto.updatePublicChannel request);

    void delete(UUID channelId);
}