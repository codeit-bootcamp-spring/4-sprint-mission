package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class FileChannelService implements ChannelService{

    private static final FileChannelService instance = new FileChannelService();
    private final ChannelRepository channelRepository;

    private FileChannelService() {
        this.channelRepository = new FileChannelRepository();
    }

    public static FileChannelService getInstance() {
        return instance;
    }

    //채널 생성
    @Override
    public Channel createChannel(Channel channel) {
        return channelRepository.save(channel);
    }

    //채널 조회
    @Override
    public List<Channel> getAllChannels() {

        return channelRepository.findAll();
    }

    @Override
    public Channel getChannelById(String id) {
        return channelRepository.findById(id);
    }

    @Override
    public Channel updateChannel(String channelId, String newName, String newDescription) {
        Channel channel = channelRepository.findById(channelId);
        if(channel != null) {
            channel.setChannelname(newName);
            channel.setDescription(newDescription);
            channel.setUpdatedAt(System.currentTimeMillis());

            channelRepository.deleteById(channelId);
            return channelRepository.save(channel);
        } else {
            throw new IllegalArgumentException("Channel not found");
        }
    }

    @Override
    public Channel deleteChannel(String channelId) {
        return channelRepository.findById(channelId);
    }
}