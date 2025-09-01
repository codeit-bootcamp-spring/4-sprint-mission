package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  //
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  // 생성 (PUBLIC)
  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();

    log.info("Create Channel requested: type=PUBLIC, name='{}'", name);

    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);

    log.info("Create Channel succeeded: type=PUBLIC, id={}, name='{}'", channel.getId(), name);

    return channelMapper.toDto(channel);
  }

  // 생성 (PRIVATE)
  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {

    log.info("Create Channel requested: type=PRIVATE, participantCount={}",
            request.participantIds() == null ? 0 : request.participantIds().size());

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("Create Channel succeeded: type=PRIVATE, id={}, participantsPersisted={}",
            channel.getId(), readStatuses.size());

    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();

    log.info("Update Channel requested: id={}", channelId);

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              ChannelNotFoundException channelNotFoundException = new ChannelNotFoundException(channelId);
              log.debug("Update Channel rejected: not found: id={}", channelId, channelNotFoundException);
              return channelNotFoundException;
            });

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      PrivateChannelUpdateException privateChannelUpdateException = new PrivateChannelUpdateException(channelId);
      log.debug("Update Channel rejected: private channel cannot be updated: id={}", channelId, privateChannelUpdateException);
      throw privateChannelUpdateException;
    }
    channel.update(newName, newDescription);

    log.info("update Channel succeeded: id={}", channelId);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {

    log.info("Delete Channel requested: id={}", channelId);

    if (!channelRepository.existsById(channelId)) {
      ChannelNotFoundException channelNotFoundException = new ChannelNotFoundException(channelId);
      log.debug("Delete Channel rejected: not found: id={}", channelId, channelNotFoundException);
      throw channelNotFoundException;
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);

    log.info("Delete Channel succeeded: id={}", channelId);
  }
}
