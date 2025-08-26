package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelArgumentException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
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
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.info("[ChannelService] Create public channel started - name: {}", request.name());

    if (request.name() == null || request.name().isEmpty()) {
      log.warn("[ChannelService] Create failed - channel name is empty");
      throw new InvalidChannelArgumentException(
          Map.of("name", request.name() != null ? request.name() : ""));
    }

    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channelRepository.save(channel);

    log.info("[ChannelService] Create public channel completed - channelId: {}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.info("[ChannelService] Create private channel started");

    if (request.participantIds() == null || request.participantIds().isEmpty()) {
      log.warn("[ChannelService] Create private channel failed - participants is empty");
      throw new InvalidChannelArgumentException(Map.of("participants", List.of()));
    }
    var participants = userRepository.findAllById(request.participantIds());

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    var readStatuses =
        participants.stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("[ChannelService] Create private channel completed - channelId: {}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository
        .findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds =
        readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

    return channelRepository
        .findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.info("[ChannelService] Update channel started - channelId: {}", channelId);

    Channel channel =
        channelRepository
            .findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn(
          "[ChannelService] Update failed - private channel cannot be updated - channelId: {}",
          channelId);
      throw new PrivateChannelUpdateException(Map.of("channelId", channelId));
    }

    channel.update(request.newName(), request.newDescription());

    log.info("[ChannelService] Update channel completed - channelId: {}", channelId);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.info("[ChannelService] Delete channel started - channelId: {}", channelId);

    if (!channelRepository.existsById(channelId)) {
      log.warn("[ChannelService] Delete failed - channel not found - channelId: {}", channelId);
      throw new ChannelNotFoundException(Map.of("channelId", channelId));
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);

    log.info("[ChannelService] Delete channel completed - channelId: {}", channelId);
  }
}
