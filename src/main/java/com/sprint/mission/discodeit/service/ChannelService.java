package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    // Create
    public Optional<Channel> addChannel(String name, User hostUser);

    // Read
    public List<Channel> getChannels();

    public Optional<Channel> getChannelById(UUID id);

    // Update
    public void updateChannelNameById(UUID channelId, UUID hostUserId, String name);

    // Delete
    public void deleteChannelById(UUID id, UUID hostUserId);


}
