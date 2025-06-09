package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Set;

public interface ChannelService {
    Channel createChannel(String channelName, String description);
    Channel getChannelById(String channelId);
    Set<Channel> getAllChannels();
    Channel updateChannel(String channelId, String newName, String newDescription);
    Channel deleteChannel(String channelId);

}

