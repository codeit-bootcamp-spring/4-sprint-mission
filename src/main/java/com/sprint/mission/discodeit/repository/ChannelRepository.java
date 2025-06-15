package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    // Create
    public void addChannelAndSave(Channel channel, User hostUser);

    // Read
    public List<Channel> getAll();

    public Channel getById(UUID id);

    // Update
    public void updateChannelName(Channel channel, String name);

    // Delete
    public void delete(Channel channel, User hostUser);

    public void deleteAll();

}
