package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto create(PublicChannelCreateRequest request);
    ChannelResponseDto create(PrivateChannelCreateRequest request);
    ChannelResponseDto find(UUID channelId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    ChannelResponseDto update(UUID channelId, PublicChannelUpdateRequest request);
    void delete(UUID channelId);
}