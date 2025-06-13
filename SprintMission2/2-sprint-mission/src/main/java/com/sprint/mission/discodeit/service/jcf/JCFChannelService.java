package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
  private final List<Channel> data;

  public JCFChannelService() {
    this.data = new ArrayList<>();
  }

  @Override
  public Channel createChannel(String name, User user) {
    Channel channel = new Channel(name, user);
    data.add(channel);
    return channel;
  }

  @Override
  public List<Channel> findAllChannel() {
    return data.stream()
        .filter(channel -> channel.getState() != ChannelState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Channel> findChannel(UUID channelId) {
    return data.stream()
        .filter(channel -> channel.getChannelId().equals(channelId))
        .filter(channel -> channel.getState() != ChannelState.DELETED)
        .findFirst();
  }

  @Override
  public List<Channel> findChannelsByNameContains(String channelName) {
    return data.stream()
        .filter(channel -> channel.getChannelName().contains(channelName))
        .filter(channel -> channel.getState() != ChannelState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> findMessagesByUserInChannel(Channel channel, User user) {
    return channel.getChannelMessages().stream()
        .filter(message -> message.getUser().equals(user))
        .filter(message -> message.getState() != MessageState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public List<User> findUsersInChannel(Channel channel) {
    return data.stream()
        .filter(c -> c.getChannelId().equals(channel.getChannelId()))
        .flatMap(c -> c.getUsers().stream())
        .filter(user -> user.getState() != UserState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> findMessagesInChannel(Channel channel) {
    return data.stream()
        .filter(c -> c.getChannelId().equals(channel.getChannelId()))
        .flatMap(c -> c.getChannelMessages().stream())
        .filter(message -> message.getState() != MessageState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public void updateChannelName(UUID ChannelId, String updatedText) {
    data.stream()
        .filter(channel -> channel.getState() != ChannelState.DELETED)
        .filter(channel -> channel.getChannelId().equals(ChannelId))
        .findFirst()
        .ifPresent(
            channel -> {
              channel.setChannelName(updatedText);
              channel.setUpdatedAt();
            });
  }

  @Override
  public void joinUser(Channel channel, User user) {
    if (channel.getState() != ChannelState.DELETED) {
      channel.addUser(user);
    }
  }

  @Override
  public void leaveUser(Channel channel, User user) {
    if (channel.getState() != ChannelState.DELETED) {
      channel.removeUser(user);
    }
  }

  @Override
  public void deleteChannel(Channel channel) {
    if (channel.getState() != ChannelState.DELETED) {
      channel.deleteChannelState();
      data.remove(channel);
    }
  }
}
