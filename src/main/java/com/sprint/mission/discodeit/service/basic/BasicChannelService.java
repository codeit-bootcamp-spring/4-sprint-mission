package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    Channel channel = channelMapper.mapToEntity(request);

    return channelMapper.mapToDto(channelRepository.save(channel));
  }

  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    List<User> users = request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("User with id " + userId + " not found!")))
        .toList();

    List<ReadStatus> readStatuses = users.stream()
        .map(user -> new ReadStatus(user, createdChannel, createdChannel.getCreatedAt()))
        .toList();

    readStatusRepository.saveAll(readStatuses);

    return channelMapper.mapToDto(createdChannel, users);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    return channelMapper.mapToDto(channel);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    List<UUID> myPrivateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel -> channel.getType().equals(ChannelType.PUBLIC) ||
            myPrivateChannelIds.contains(channel.getId()))
        .map(channel -> {
          List<User> participants = readStatusRepository.findAllByChannelId(channel.getId())
              .stream()
              .map(ReadStatus::getUser)
              .toList();
          return channelMapper.mapToDto(channel, participants);
        })
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    channel.setName(request.newName());
    channel.setDescription(request.newDescription());
    Channel updatedChannel = channelRepository.save(channel);

    return channelMapper.mapToDto(updatedChannel);
  }

  @Override
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " not found");
    }
    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);
  }
}
