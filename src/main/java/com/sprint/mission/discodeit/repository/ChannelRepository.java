package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface ChannelRepository {
    Channel createChannel(User user, String channelName);
    void deleteChannel(User user, Channel channel);
    void addUserToChannel(User user, Channel channelName);
    void leaveUserFromChannel(User user, Channel channel);
    void updateChannelName(User user, Channel channel, String newName);
    void updateHostUser(User oldHostUser, Channel channel, User newHostUser);

    List<Channel> getAllChannels();
}
