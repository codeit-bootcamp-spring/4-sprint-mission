package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDTO);

  Channel createPrivateChannel(
      CreatePrivateChannelRequestDto createPrivateChannelRequestDto);

  void deleteChannel(UUID channelId);


  List<ChannelDto> findChannelDtoListByUserId(UUID userId);

  Channel updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest);

  Channel findChannelByChannelId(UUID channelId);
}
