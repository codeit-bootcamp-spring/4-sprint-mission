package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel createChannel(Channel channel) {
        data.add(channel);
        return channel;
    }


    @Override
    public Optional<Channel> getChannelById(UUID channelId) {
        return data.stream()
                .filter(ch -> ch.getId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<Channel> getChannels() {
        return data.stream()
                .toList();
    }

    @Override
    public Optional<Channel> updateChannel(UUID channelId, String newChannelName) {
        return data.stream()
                .filter(ch -> ch.getId().equals(channelId))
                .findFirst()
                .map(channel -> {
                    channel.updateName(newChannelName);
                    return channel;
                });
    }

    @Override
    public void joinUserToChannel(Channel channel, User user) {
        if (user.getStatus().equals(UserStatus.ACTIVE)) {
            channel.addUser(user);
        }
    }

    @Override
    public void deleteUserToChannel(Channel channel, User user) {
        channel.deleteUser(user);
    }


    @Override
    public void deleteChannel(Channel channel) {
        data.removeIf(ch -> ch.getId().equals(channel.getId()));
    }
}
