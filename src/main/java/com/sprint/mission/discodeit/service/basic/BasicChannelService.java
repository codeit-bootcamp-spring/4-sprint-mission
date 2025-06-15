package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class BasicChannelService {
    ChannelRepository channelRepository;

    private BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    private static BasicChannelService basicChannelService;

    public static BasicChannelService getInstance(ChannelRepository channelRepository) {
        if(basicChannelService == null){
            basicChannelService = new BasicChannelService(channelRepository);
        }
        return basicChannelService;
    }

    public Channel saveChannel(Path dir, Channel channel) {
        return channelRepository.saveChannel(dir, channel);
    }
    public List<Channel> loadAllChannels(Path dir) {
        return channelRepository.loadAllChannels(dir);
    }
    public Optional<Channel> loadChannelById(Path dir, UUID id) {
        return channelRepository.loadChannelById(dir, id);
    }
    public Optional<Channel> loadChannelByChannelName(Path dir, String channelName) {
        return channelRepository.loadChannelByChannelName(dir, channelName);
    }
    public void updateChannelById(Path dir, UUID id, String updateChannelName) {
        loadChannelById(dir, id).ifPresent(channel -> {
            channel.setChannelName(updateChannelName);
            channel.setUpdatedAt(System.currentTimeMillis());
            saveChannel(dir, channel);
        });
    }
    public void updateChannelByChannelName(Path dir, String channelName, String updateChannelName) {
        loadChannelByChannelName(dir, channelName).ifPresent(channel -> {
            channel.setChannelName(updateChannelName);
            channel.setUpdatedAt(System.currentTimeMillis());
            saveChannel(dir, channel);
        });
    }
    public void deleteChannel(Path dir, Channel channel) {
        channelRepository.deleteChannel(dir, channel);
    }
}
