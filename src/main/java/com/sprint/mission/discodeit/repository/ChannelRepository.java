package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    void createChannel(Channel channel);
    void deleteChannel(Channel channel);

    void saveChannels(List<Channel> channels);
    List<Channel> loadChannels();

    void updateChannel(Channel channel);

    void deleteUserFromChannels(User user);
    Optional<Channel> findChannelByChannelName(String channelName);
    Optional<Channel> findChannelByChannelId(UUID channelId);
    List<Channel> findChannelsByUserId(UUID userId);
}