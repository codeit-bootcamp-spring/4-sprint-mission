package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public void addChannel(Channel channel);
    public List<Channel> getChannels();
    public void deleteChannel(UUID channelId);
    public void updateChannel(UUID channelId, int selectedNum, String updatedText);
    public void joinChannel(UUID channelId, UUID userId);
    public void leaveChannel(UUID channelId, UUID userId);
    public void findChannelByKeyword(String keyword);
}

