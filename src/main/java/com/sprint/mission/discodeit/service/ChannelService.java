package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.ChannelType;
=======
import com.sprint.mission.discodeit.entity.Channel;
>>>>>>> b9e39681b59d5df773deee8369d6d1b81958f2f6

import java.util.List;
import java.util.UUID;

public interface ChannelService {
<<<<<<< HEAD
    ChannelResponseDto create(ChannelType type, String name, String description);
    ChannelResponseDto createPublicChannel(PublicChannelCreateDto publicChannelCreateDto);
    ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto publicChannelCreateDto);
    ChannelResponseDto find(UUID channelId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    ChannelResponseDto update(ChannelUpdateRequestDto channelUpdateRequestDto);
    void delete(UUID channelId);
=======
    Channel createChannel(Channel name);
    Channel getChannel(UUID name);
    List<Channel> getChannels();
    void updateChannel(UUID name, String Channel);
    boolean deleteChannel(UUID name);

>>>>>>> b9e39681b59d5df773deee8369d6d1b81958f2f6
}
