package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private static JCFChannelRepository jcfChannelRepository;
    private final List<Channel> data;

    public JCFChannelRepository() {this.data = new ArrayList<>(); }

    public static JCFChannelRepository getInstance() {
        if (jcfChannelRepository == null) {
            jcfChannelRepository = new JCFChannelRepository();
        }
        return jcfChannelRepository;
    }

    @Override
    public Channel saveChannel(Path dir, Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public List<Channel> loadAllChannels(Path dir) {
        return data.stream()
                .toList();
    }

    @Override
    public Optional<Channel> loadChannelById(Path dir, UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Channel> loadChannelByChannelName(Path dir, String channelName) {
        return data.stream()
                .filter(channel -> channel.getChannelName().equals(channelName))
                .findFirst();
    }

    @Override
    public void deleteChannel(Path dir, Channel channel) {
        data.remove(channel);
    }
}
