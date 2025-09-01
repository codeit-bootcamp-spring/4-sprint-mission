package com.codeit.discodeit8.service;

import com.codeit.discodeit8.dto.channel_service_dto.*;
import com.codeit.discodeit8.entity.Channel;
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
