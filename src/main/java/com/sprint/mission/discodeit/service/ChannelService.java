package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
=======
import com.sprint.mission.discodeit.entity.Channel;
>>>>>>> fe56ded0b57ffcf4521001ca7a956c8f5baf981f

import java.util.List;
import java.util.UUID;

public interface ChannelService {
<<<<<<< HEAD
    ChannelResponseDto createPublicChannel(PublicChannelCreateDto dto);

    ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto);

    ChannelResponseDto find(UUID channelId);

    List<ChannelResponseDto> findAllByUserId(UUID userId);

    ChannelResponseDto update(ChannelUpdateDto channelUpdateDto);

    void delete(UUID channelId);
=======
    Channel createChannel(Channel name);
    Channel getChannel(UUID name);
    List<Channel> getChannels();
    void updateChannel(UUID name, String Channel);
    boolean deleteChannel(UUID name);

>>>>>>> fe56ded0b57ffcf4521001ca7a956c8f5baf981f
}
