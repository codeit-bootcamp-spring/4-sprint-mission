package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        this.data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public List<Channel>findAllById(Iterable<UUID> ids) {
        Set<UUID> idSet = new HashSet<>();
        ids.forEach(idSet::add);

        return this.data.values().stream()
                .filter(channel -> idSet.contains(channel.getId()))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public boolean existsByName(String name) {
        return this.data.containsKey(name);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
