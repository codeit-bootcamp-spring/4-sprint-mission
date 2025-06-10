package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFchannelService implements ChannelService {
    private final Set<Channel> data;

    public JCFchannelService() {
        this.data = new HashSet<>();
    }


    @Override
    public Channel createChannel(String channelName, String description) {
        Channel channel = new Channel(channelName, description);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel getChannelById(String channelId) {
        for (Channel channel : data) {
            if (channel.getChannelId().equals(channelId)) {
                return channel;
            }
        } return null;
    }

    @Override
    public Set<Channel> getAllChannels() {
        return new HashSet<>(data);
    }

    @Override
    public Channel updateChannel(String channelId, String newName, String newDescription) {
        for (Channel channel : data) {
            if (channel.getChannelId().equals(channelId)) {
                channel.setChannelname(newName);
                channel.setDescription(newDescription);
                channel.setUpdatedAt(System.currentTimeMillis());

                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel deleteChannel(String channelId) {
        for (Channel channel : data) {
            if (channel.getChannelId().equals(channelId)) {
                data.remove(channel);
                return channel;
            }
        }
        return null;
    }
}

