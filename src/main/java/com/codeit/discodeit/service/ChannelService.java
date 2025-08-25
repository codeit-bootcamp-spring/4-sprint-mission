package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
  ChannelDto createPublicChannel(CreatePublicChannelRequestDto createPublicChannelRequestDto);
  ChannelDto createPrivateChannel(List<UUID> userIdList);

  void deleteChannel(UUID channelId);

  ChannelDto updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest);

  List<ChannelDto> findChannelListByUserId(UUID userId);
  Channel findChannelByChannelId(UUID channelId);
}
