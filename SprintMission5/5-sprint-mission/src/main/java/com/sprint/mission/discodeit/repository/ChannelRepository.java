package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

  ChannelEntity save(ChannelEntity channelEntity);

  List<ChannelEntity> findAll();

  Optional<ChannelEntity> findById(UUID id);

  void delete(UUID id);

  List<ChannelEntity> findChannelsByNameContains(String channelName);
}
