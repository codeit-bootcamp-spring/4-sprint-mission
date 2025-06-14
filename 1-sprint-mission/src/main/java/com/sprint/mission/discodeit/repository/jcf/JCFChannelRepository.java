package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<String, Channel> data = new HashMap<>();

    private static final JCFChannelRepository instance = new JCFChannelRepository();

    private JCFChannelRepository() {}

    public static JCFChannelRepository getInstance() {
        return instance;
    }

    public Channel save(Channel channel) {
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel findById(String channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found");
        }
        return channel;
    }

    @Override
    public Channel deleteById(String channelId) {
        Channel removed = data.remove(channelId);
        if (removed == null) {
            throw new IllegalArgumentException("Channel not found");
        } else {
            removed.getMessages().clear();
        }
        return removed;
    }
}
