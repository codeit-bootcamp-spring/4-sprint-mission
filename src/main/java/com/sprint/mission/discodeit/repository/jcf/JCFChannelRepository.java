package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private static final List<Channel> channels = new ArrayList<>();

    // Create
    @Override
    public void addChannelAndSave(Channel channel, User hostUser) {
        channels.add(channel);
        channel.addUserToActiveChannel(hostUser);
        hostUser.addChannel(channel);
    }

    // Read
    @Override
    public List<Channel> getAll() {
        return channels;
    }

    @Override
    public Channel getById(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No channel with id " + id + " found"));
    }

    // Update
    @Override
    public void updateChannelName(Channel channel, String name) {
        channel.setChannelName(name);
        channel.setUpdatedAt(System.currentTimeMillis());
    }

    // Delete
    @Override
    public void delete(Channel channel, User hostUser) {
        channel.removeAllUsersAndMessagesFromActiveChannel();
        channels.remove(channel);
    }

    @Override
    public void deleteAll() {
        channels.forEach(Channel::removeAllUsersAndMessagesFromActiveChannel);
        channels.clear();
    }
}
