package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private static JCFChannelRepository instance;
    private final Map<UUID, Channel> data = new HashMap<UUID, Channel>();

    private JCFChannelRepository() {}

    public static JCFChannelRepository getInstance() {
        if (instance == null) {
            instance = new JCFChannelRepository();
        }
        return instance;
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        data.remove(channelId);
    }

    @Override
    public boolean isContains(UUID channelId) {
        return data.containsKey(channelId);
    }

    @Override
    public Channel findById(UUID channelId) {
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<Channel>(data.values());
    }

    @Override
    public List<Channel> findByName(String name) {
        return data.values().stream()
                .filter(c -> c.getChannelName().equals(name))
                .toList();
    }
}

