package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(PublicChannelCreateRequest dto);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest dto);
//    ChannelResponse findById(UUID channelId, UUID userId);
    List<UserChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(UUID channelId, PublicChannelUpdateRequest dto);
    void delete(UUID channelId);
}
