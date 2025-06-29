package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelCreateResponse createPublicChannel(PublicChannelRequest request);
    ChannelCreateResponse createPrivateChannel(PrivateChannelRequsest request);
    ChannelDetailResponse find(UUID channelId);
    List<ChannelDetailResponse> findAll();
    List<ChannelDetailResponse> findAllByUserId(UUID userId);
    ChannelCreateResponse update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
