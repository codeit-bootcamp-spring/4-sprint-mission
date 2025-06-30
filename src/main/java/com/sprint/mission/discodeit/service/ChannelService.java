package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public Channel createChannel(String name);
    public Channel searchChannel(UUID id);
    public List<Channel> searchAll();
    public Channel updateChannel(UUID id, String newName);
    public void deleteChannel(UUID id);

}
