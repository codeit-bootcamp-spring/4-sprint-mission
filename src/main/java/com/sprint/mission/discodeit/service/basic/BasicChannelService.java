package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;

  @Override
  public ChannelResponse createPublic(PublicChannelCreateRequest request) {

    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    return channelMapper.toChannelResponse(channelRepository.save(channel));

  }

  @Override
  public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
    List<UUID> userIds = request.participantIds();

    for (UUID userId : userIds) {
      if (!userRepository.existsById(userId)) {
        throw new NoSuchElementException("User id " + userId + " does not exist");
      }
    }

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    request.participantIds().stream()
        .map(userId -> new ReadStatus(userId, createdChannel.getId()))
        .forEach(readStatusRepository::save);

    return channelMapper.toChannelResponse(createdChannel);

  }

  @Override
  public ChannelResponse find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

//    // get all the ReadStatus from one channel and get the user ids from it
//    List<UUID> userIds = null;
//    if (channel.getType() == ChannelType.PRIVATE) {
//      userIds = readStatusRepository.findByChannelId(channel.getId())
//          .stream()
//          .map(ReadStatus::getUserId)
//          .toList();
//    }

    return channelMapper.toChannelResponse(channel);
  }

//  @Override
//  public List<ChannelResponse> findAllByUserId(UUID userId) {
//
//    List<UUID> mySubscribedChannelIds = readStatusRepository.findByUserId(userId).stream()
//        .map(ReadStatus::getChannelId)
//        .toList();
//
//    return channelRepository.findAll().stream()
//        .filter(channel ->
//            channel.getType().equals(ChannelType.PUBLIC)
//                || mySubscribedChannelIds.contains(channel.getId())
//        )
//        .map(channelMapper::toChannelResponse)
//        .toList();
//
//  }

  public List<UserChannelResponse> findAllByUserId(UUID userId) {

    List<Channel> allChannels = channelRepository.findAll();

    return allChannels
        .stream()
        .filter(c -> {
              // if current channel is private, then get only the private channels that includes userId
              if (c.getType() == ChannelType.PRIVATE) {
                Optional<ReadStatus> readStatus = readStatusRepository.findByChannelAndUserId(
                    c.getId(),
                    userId);
                return readStatus.isPresent();

              } else {
                return c.getType() == ChannelType.PUBLIC;
              }
            }
        )
        .map(c -> {

          // getting users in private channel
          List<UUID> userIds = null;
          if (c.getType() == ChannelType.PRIVATE) {
            userIds = readStatusRepository.findByChannelId(c.getId())
                .stream()
                .map(ReadStatus::getUserId)
                .toList();
          }

          // getting latest message in channel
          Instant lastMessage = messageRepository.findByChannelId(c.getId())
              .stream()
              .map(BaseEntity::getCreatedAt)
              .max(Instant::compareTo)
              .orElse(null);

          return channelMapper.toUserChannelResponse(c, userIds, lastMessage);

        }).toList();
  }


  @Override
  public ChannelResponse update(UUID channelId,
      PublicChannelUpdateRequest request) {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    String newName = request.newName();
    String newDescription = request.newDescription();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(newName, newDescription);
    return channelMapper.toChannelResponse(channelRepository.save(channel));
  }

  @Override
  public void delete(UUID id) {
    if (!channelRepository.existsById(id)) {
      throw new NoSuchElementException("Channel with id " + id + " not found");
    }
    channelRepository.deleteById(id);

    // deleting all messages
    List<Message> messageList = messageRepository.findByChannelId(id);
    for (Message message : messageList) {
      messageRepository.deleteById(message.getId());
    }

    // deleting all read statuses
    List<ReadStatus> readStatusList = readStatusRepository.findByChannelId(id);
    for (ReadStatus readStatus : readStatusList) {
      readStatusRepository.deleteById(readStatus.getId());
    }
  }

  @Override
  public void deleteAll() {
    channelRepository.deleteAll();
  }
}
