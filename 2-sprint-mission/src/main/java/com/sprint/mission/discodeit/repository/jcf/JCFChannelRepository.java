package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelState;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new ConcurrentHashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return data.values().stream()
                .filter(channel -> channel.getState()!= ChannelState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null || channel.getState() == ChannelState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    @Override
    public void delete(Channel channel) {
        Channel existing = data.get(channel.getChannelId());
        if (existing != null && existing.getState() != ChannelState.DELETED) {
            existing.deleteChannelState();
        }
    }

    @Override
    public List<Channel> findChannelsByNameContains(String channelName) {
        return data.values().stream()
                .filter(channel -> channel.getChannelName().contains(channelName))
                .collect(Collectors.toList());
    }
}
