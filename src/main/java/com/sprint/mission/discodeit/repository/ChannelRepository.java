package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    Channel save(Channel channel);

    void delete(UUID id);

    boolean isContains(UUID id);

    Channel findById(UUID id);

    List<Channel> findAll();

    List<Channel> findByName(String name);

}
