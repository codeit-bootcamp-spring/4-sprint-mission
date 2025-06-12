package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFchannelService implements ChannelService {
    private final Map<String, Channel> data;
    private static final JCFchannelService instance = new JCFchannelService();

    private JCFchannelService() {
        this.data = new HashMap<>();
    }

    public static JCFchannelService getInstance() {
        return instance;
    }


    @Override
    public Channel createChannel(Channel channel) {
        data.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public Channel getChannelById(String channelId) {
        if (!data.containsKey(channelId)) {
            throw new IllegalArgumentException("Channel not found");
        } else {
            return data.get(channelId);
        }
    }

    @Override
    public Set<Channel> getAllChannels() {
        return new HashSet<>(data.values());
    }

    @Override
    public Channel updateChannel(String channelId, String newName, String newDescription) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found");
        } else {
            channel.setChannelname(newName);
            channel.setDescription(newDescription);
            channel.setUpdatedAt(System.currentTimeMillis());
        }
        return channel;
    }

    @Override
    public Channel deleteChannel(String channelId) {
        Channel removed = data.remove(channelId);
        if (removed != null) {
            removed.getMessages().clear();
        }
        return removed;
    }
}

