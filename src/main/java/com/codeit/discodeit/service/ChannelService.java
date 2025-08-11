package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
  Channel createPublicChannel(Channel channel);
  Channel createPrivateChannel (Channel channel, List<UUID> userIdList);

  void deleteChannel(UUID channelId);

  Channel updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest);

  List<Channel> findChannelListByUserId(UUID userId);
  Channel findChannelByChannelId(UUID channelId);
}
