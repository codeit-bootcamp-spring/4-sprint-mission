package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(Channel name);
    Channel getChannel(UUID name);
    List<Channel> getChannels();
    void updateChannel(UUID name, String Channel);
    boolean deleteChannel(UUID name);

}
