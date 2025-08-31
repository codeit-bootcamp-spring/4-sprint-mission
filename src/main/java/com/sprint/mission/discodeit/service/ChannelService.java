package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    public Channel createChannel(User user, String channelName);
    public List<Channel> getChannels();
    public Optional<Channel> getChannelById(UUID channelId);
    public void updateChannel(UUID channelId, String channelName);
    public void deleteChannel(Channel channel);
    public void removeUserFromChannel(Channel channel, UUID userId);
    public void addUserToChannel(Channel channel, User user);
}
