package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    void createChannel(Channel channel);
    void deleteChannel(Channel channel);
    void updateChannel(Channel channel);

    List<Channel> loadChannels();
    List<Channel> findAllPublicChannel();

    Optional<Channel> findChannelByChannelName(String channelName);
    Optional<Channel> findChannelByChannelId(UUID channelId);
}