package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    Channel save(Channel channel);

    List<Channel> findAll();

    Optional<Channel> findById(UUID id);

    void delete(Channel channel);

    List<Channel> findChannelsByNameContains(String channelName);
}
