package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    Channel saveChannel(Path dir, Channel channel);

    List<Channel> loadAllChannels(Path dir);
    Optional<Channel> loadChannelById(Path dir, UUID id);
    Optional<Channel> loadChannelByChannelName(Path dir, String Channel);

    void deleteChannel(Path dir, Channel channel);
}
