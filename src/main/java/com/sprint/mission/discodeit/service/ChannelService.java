package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    public Channel createChannel(String name, User hostUser);

    // Read
    public List<Channel> getChannels();

    public Channel getChannelById(UUID id);

    // Update
    public void updateChannelNameByChannel(Channel channel, String name);

    // Delete
    public void deleteChannelByChannelAndHostUser(Channel channel, User hostUser);

    public void deleteAllChannels();

}
