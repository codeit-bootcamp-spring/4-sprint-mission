package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(Channel channel);
    Optional<Channel> getChannelById(UUID chanelId);
    List<Channel> getChannels();
    Optional<Channel> updateChannel(UUID channelId, String newChannelName);
    void joinUserToChannel(Channel channel, User user);
    void deleteUserToChannel(Channel channel, User user);
    void deleteChannel(Channel channel);
}
