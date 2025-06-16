package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public interface ChannelService {

    Channel createChannel(User user, String channelName);
    void deleteChannel(User user, Channel channel);
    void addUserToChannel(User user, Channel channelName);
    void leaveUserFromChannel(User user, Channel channel);
    void updateChannelName(User user, Channel channel, String newName);
    void updateHostUser(User oldHostUser, Channel channel, User newHostUser);

    void printChannel(Channel channel);
    void printAllChannels();
    void printUsersFromChannel(Channel channel);
}
