package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFchannelService implements ChannelService {

    private final JCFChannelRepository repository;

    private static final JCFchannelService instance = new JCFchannelService();

    private JCFchannelService() {
        this.repository = JCFChannelRepository.getInstance();
    }

    public static JCFchannelService getInstance() {
        return instance;
    }


    @Override
    public Channel createChannel(Channel channel) {
        return repository.save(channel);
    }

    @Override
    public Channel getChannelById(String channelId) {
        return repository.findById(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return repository.findAll();
    }

    @Override
    public Channel updateChannel(String channelId, String newName, String newDescription) {
        Channel channel = repository.findById(channelId);
        if(channel != null) {
            channel.setChannelname(newName);
            channel.setDescription(newDescription);
            channel.setUpdatedAt(System.currentTimeMillis());

            repository.deleteById(channelId);
            return repository.save(channel);
        } else {
            throw new IllegalArgumentException("Channel not found");
        }
    }

    @Override
    public Channel deleteChannel(String channelId) {

        return repository.deleteById(channelId);
    }
}

