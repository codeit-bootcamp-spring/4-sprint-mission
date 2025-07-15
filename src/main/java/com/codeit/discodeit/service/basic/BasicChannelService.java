package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateUserException;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.exception.exception.PrivateChannelUpdateNotAllowedException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public Channel createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDto) {
    Optional<Channel> duplicateChannel = channelRepository.findChannelByChannelName(
        createPublicChannelRequestDto.getName());
    optionalChannelIsPresent(duplicateChannel);

    Channel channel = new Channel(
        createPublicChannelRequestDto.getName(),
        createPublicChannelRequestDto.getDescription());

    List<User> users = userRepository.loadUsers();

    for (User user : users) {
      channel.addUser(user);
    }

    //channel.addUser(hostUser);
    //hostUser.addChannel(channel);

    channelRepository.createChannel(channel);
    //userRepository.updateUser(hostUser);

    //createOrUpdateReadStatus(hostUser, channel);

    //List<Message> messages = messageRepository.findMessagesByChannelId(channel.getId());
    //return new ChannelResponseDto(channel, messages.get(messages.size()-1).getId());
    return channel;
  }

  @Override
  public Channel createPrivateChannel(
      CreatePrivateChannelRequestDto createPrivateChannelRequestDto) {

    ArrayList<UUID> enterUserIds = createPrivateChannelRequestDto.getParticipantIds();

    Channel channel = new Channel();
    //channel.addUser(hostUser);
    for (UUID enterUserId : enterUserIds) {
      Optional<User> enterUser = userRepository.findUserByUserId(enterUserId);
      if (enterUser.isPresent()) {
        User user = enterUser.get();
        channel.addUser(user);
        user.addChannel(channel);
        userRepository.updateUser(user);
      }
    }
    //hostUser.addChannel(channel);

    channelRepository.createChannel(channel);
    //userRepository.updateUser(hostUser);

    return channel;
  }

  @Override
  public void deleteChannel(UUID channelId) {

    Channel channel = findChannelByChannelId(channelId);

    // 채널에서 유저 제거 같은 비즈니스 로직 수행
    List<User> users = userRepository.loadUsers();
    for (User u : users) {
      u.removeChannel(channel);
      u.getMessageIds().removeIf(messageId -> channel.getMessageIds().contains(messageId));
    }

    readStatusRepository.deleteReadStatusByChannelId(channel.getId());
    messageRepository.deleteMessagesByChannelId(channel.getId());
    userRepository.saveUsers(users);
    channelRepository.deleteChannel(channel);
  }

  @Override
  public List<ChannelDto> findChannelDtoListByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findChannelsByUserId(userId);
    List<ChannelDto> channelDtoList = new ArrayList<>();
    for (Channel channel : channels) {
      Message lastMessageInChannel = messageRepository.findLastMessageInChannel(channel.getId());
      channelDtoList.add(new ChannelDto(channel, lastMessageInChannel));
    }
    return channelDtoList;
  }

  @Override
  public Channel updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = findChannelByChannelId(channelId);

    if (channel.getChannelType().equals(ChannelType.PRIVATE)) {
      throw new PrivateChannelUpdateNotAllowedException("Private Channel은 수정할 수 없음",
          (channelId + "는 Private Channel 입니다."));
    }

    channel.setChannelName(publicChannelUpdateRequest.getNewName());
    channel.setChannelDescription(publicChannelUpdateRequest.getNewDescription());
    channelRepository.updateChannel(channel);

    return channel;
  }

  private void optionalChannelIsPresent(Optional<Channel> channel) {
    if (channel.isPresent()) {
      throw new DuplicateUserException("Channel 정보가 중복됩니다.",
          channel.get().getChannelName() + "이름이 중복됩니다.");
    }
  }

  @Override
  public Channel findChannelByChannelId(UUID channelId) {
    return channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(() -> new NoFindChannelException("Channel을 찾을 수 없음",
            ("Channel with id {" + channelId + "} not found")));
  }

  private User findUserByUserId(UUID userId) {
    return userRepository.findUserById(userId)
        .orElseThrow(
            () -> new NoFindUserException("해당 유저를 찾을 수 없습니다.", userId + "유저를 찾을 수 없습니다."));
  }
}
