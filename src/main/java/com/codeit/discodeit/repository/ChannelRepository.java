package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.Channel;

import com.codeit.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    List<Channel> findAllByType(ChannelType type);
    Optional<Channel> findByName(String channelName);
}