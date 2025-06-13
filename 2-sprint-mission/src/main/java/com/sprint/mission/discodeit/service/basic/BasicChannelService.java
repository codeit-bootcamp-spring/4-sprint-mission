package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository , UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel createChannel(String name, User user) {
        Channel channel = new Channel(name, user);
        return channelRepository.save(channel);
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public Optional<Channel> findChannel(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findChannelsByNameContains(String channelName) {
        return channelRepository.findChannelsByNameContains(channelName);
    }

    @Override
    public List<Message> findMessagesByUserInChannel(Channel channel, User user) {
        Channel findChannel = isExistChannel(channel);
        User findUser = isExistUser(user);
        return findChannel.getChannelMessages().stream()
                .filter((message -> message.getState()!=MessageState.DELETED))
                .filter(message -> message.getUser().getUserId().equals(findUser.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersInChannel(Channel channel) {
        Channel findChannel = isExistChannel(channel);
        return findChannel.getUsers();
    }

    @Override
    public List<Message> findMessagesInChannel(Channel channel) {
        Channel findChannel = isExistChannel(channel);
        return findChannel.getChannelMessages();
    }

    @Override
    public void updateChannelName(UUID ChannelId, String updatedText) {
        Channel channel = isExistChannel(ChannelId);
        channel.setChannelName(updatedText);
        channelRepository.save(channel);
    }

    @Override
    public void joinUser(Channel channel, User user) {
        Channel findChannel = isExistChannel(channel);
        User findUser = isExistUser(user);
        findChannel.addUser(findUser);
        channelRepository.save(findChannel);
    }

    @Override
    public void leaveUser(Channel channel, User user) {
        Channel findChannel = isExistChannel(channel);
        User findUser = isExistUser(user);
        findChannel.removeUser(findUser);
        channelRepository.save(findChannel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        channelRepository.delete(channel);
    }

    public Channel isExistChannel(Channel channel) {
        return channelRepository.findById(channel.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }
    public Channel isExistChannel(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }

    public User isExistUser(User user) {
        return userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}
