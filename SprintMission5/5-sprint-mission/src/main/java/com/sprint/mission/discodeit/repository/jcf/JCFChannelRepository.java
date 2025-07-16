package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {

  private final Map<UUID, ChannelEntity> data = new ConcurrentHashMap<>();

  @Override
  public ChannelEntity save(ChannelEntity channelEntity) {
    data.put(channelEntity.getId(), channelEntity);
    return channelEntity;
  }

  @Override
  public List<ChannelEntity> findAll() {
    return data.values().stream()
        .filter(channel -> channel.getState() != ChannelState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<ChannelEntity> findById(UUID id) {
    ChannelEntity channelEntity = data.get(id);
    if (channelEntity == null || channelEntity.getState() == ChannelState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(channelEntity);
  }

  @Override
  public void delete(UUID id) {
    ChannelEntity existing = data.get(id);
    if (existing != null && existing.getState() != ChannelState.DELETED) {
      existing.setToDelete();
    }
  }

  @Override
  public List<ChannelEntity> findChannelsByNameContains(String channelName) {
    return data.values().stream()
        .filter(channel -> channel.getChannelName().contains(channelName))
        .collect(Collectors.toList());
  }
}
