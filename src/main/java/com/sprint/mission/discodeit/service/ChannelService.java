package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelResponse createPublic(PublicChannelCreateRequest channelRequestDto);

  ChannelResponse createPrivate(PrivateChannelCreateRequest channelRequestDto);

  ChannelResponse find(UUID channelId);

  List<UserChannelResponse> findAllByUserId(UUID userId);

  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest channelUpdateRequestDto);

  void delete(UUID id);

  void deleteAll();
}
