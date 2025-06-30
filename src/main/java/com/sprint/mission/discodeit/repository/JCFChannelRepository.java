package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channelData;

    public JCFChannelRepository() {
        this.channelData = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        this.channelData.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findId(UUID id) {
        return Optional.ofNullable(this.channelData.get(id)); // Optional 공부
    }

    @Override
    public List<Channel> findAll() {
        return this.channelData.values().stream().toList();
    }

    @Override
    public boolean existsId(UUID id) {
        return this.channelData.containsKey(id); // boolean을 반환함
    }

    @Override
    public void deleteId(UUID id) {
        this.channelData.remove(id);
    }
}
