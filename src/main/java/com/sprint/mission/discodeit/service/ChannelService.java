package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest);

    Channel createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest);

    List<Channel> findAllByUserId(UUID userId);

//    ChannelDto findByChannelId(UUID channelId);

    Channel updateChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);

    void deleteChannel(UUID channelId);
}
