package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.RepositoryFactory;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(User user, String channelName) {
        return channelRepository.createChannel(user, channelName);
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        channelRepository.addUserToChannel(user, channel);
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        channelRepository.leaveUserFromChannel(user, channel);
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        channelRepository.updateChannelName(user, channel, newName);
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        channelRepository.deleteChannel(user, channel);
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        channelRepository.updateHostUser(oldHostUser, channel, newHostUser);
    }

    @Override
    public void printAllChannels() {
        List<Channel> channels = channelRepository.getAllChannels();
        System.out.printf("전체 채널 조회, 채널 수: %d%n", channels.size());
        channels.forEach(channel -> System.out.printf("채널 명: %s, 채널 ID: %s, 채널내 유저 수: %d%n", channel.getChannelName(), channel.getId(), channel.getUsers().size()));
    }

    @Override
    public void printChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("채널 단일 조회, 채널 이름: %s, 채널 내 유저 수 %d%n", channel.getChannelName(), channel.getUsers().size());
        System.out.printf("채절 주인: '%s', 채널 내 유저 들=%s%n", channel.getHostUser().getUserName(), channel.getUsers());
        //System.out.println(channel.toString());
    }

    @Override
    public void printUsersFromChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("%s 채널 내 유저 검색, 채널 내 유저 수: %d%n",
                channel.getChannelName(), channel.getUsers().size());

        channel.getUsers().forEach(u -> System.out.println(u.getUserName()));
    }

}
