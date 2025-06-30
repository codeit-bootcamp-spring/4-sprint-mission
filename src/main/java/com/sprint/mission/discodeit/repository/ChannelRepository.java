package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    public Channel save(Channel channel);
    public Optional<Channel> findId(UUID id);
    public List<Channel> findAll();
    public boolean existsId(UUID id);
    public void deleteId(UUID id);

}
