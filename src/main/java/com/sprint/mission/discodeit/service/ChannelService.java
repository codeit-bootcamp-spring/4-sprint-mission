package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto create(ChannelPublicCreateDto channelPublicCreateDto);

    ChannelResponseDto create(ChannelPrivateCreateDto channelPrivateCreateDto);

    ChannelResponseDto find(UUID channelId);

    List<ChannelResponseDto> findAllByUserId(UUID userId);

    ChannelResponseDto update(ChannelUpdateDto channelUpdateDto);

    void delete(UUID channelId);
}
