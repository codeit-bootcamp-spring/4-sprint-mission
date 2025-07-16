package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.Message;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusMapper readStatusMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final UserMapper userMapper;

  @Override
  public ChannelDto createPublicChannel(PublicChannelCreateRequest req) {
    if (req.name() == null) {
      throw new IllegalArgumentException("채널 생성에 필요한 필드가 누락되었습니다.");
    }
    ChannelEntity channelEntity = channelMapper.publicChannelCreateDtoToChannel(req);

    channelRepository.save(channelEntity);

    List<UUID> participantIds = new ArrayList<>();
    return channelMapper.channelToChannelResponseDto(channelEntity, null, participantIds);
  }

  @Override
  public ChannelDto createPrivateChannel(List<UUID> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      throw new IllegalArgumentException("채널에 참여할 유저 목록이 필요합니다.");
    }

    ChannelEntity channelEntity = new ChannelEntity("Private Channel", "", ChannelType.PRIVATE);

    for (UUID userId : userIds) {
      UserEntity userEntity = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다: " + userId));
      channelEntity.addUser(userEntity);
    }

    channelRepository.save(channelEntity);

    for (UUID userId : userIds) {
      ReadStatusEntity readStatusEntity = new ReadStatusEntity(userId, channelEntity.getId());
      readStatusRepository.save(readStatusEntity);
    }

    return channelMapper.channelToChannelResponseDto(channelEntity, null,
        channelEntity.getUserIds());
  }


  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> channel.getType() == ChannelType.PUBLIC || channel.getUserIds()
            .contains(userId))
        .map(channel -> {
          Instant lastMessageTime = getLastMessageTime(channel);
          List<UUID> participantIds = getParticipantIds(channel);
          return channelMapper.channelToChannelResponseDto(channel, lastMessageTime,
              participantIds);
        })
        .collect(Collectors.toList());
  }

  @Override
  public ChannelDto findChannelById(UUID channelId) {
    ChannelEntity channelEntity = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    Instant lastMessageTime = getLastMessageTime(channelEntity);
    List<UUID> participantIds = getParticipantIds(channelEntity);

    return channelMapper.channelToChannelResponseDto(channelEntity, lastMessageTime,
        participantIds);
  }

  @Override
  public List<ChannelDto> findChannelsByNameContains(String channelName) {
    return channelRepository.findAll().stream()
        .filter(
            channel -> channel.getChannelName().toLowerCase().contains(channelName.toLowerCase()))
        .map(channel -> {
          Instant lastMessageTime = getLastMessageTime(channel);
          List<UUID> participantIds = getParticipantIds(channel);
          return channelMapper.channelToChannelResponseDto(channel, lastMessageTime,
              participantIds);
        })
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> findMessagesByUserInChannel(UUID channelId, UUID userId) {
    ChannelEntity channel = isExistedChannel(channelId);
    isExistedUser(userId);

    return channel.getMessageIds().stream()
        .map(messageRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(message -> message.getState() != MessageState.DELETED)
        .filter(message -> message.getAuthorId().equals(userId))
        .map(messageMapper::messageToMessageResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public ChannelDto updateChannelName(PublicChannelUpdateRequest req) {
    ChannelEntity channelEntity = isExistedChannel(req.id());

    if (channelEntity.getType() == ChannelType.PRIVATE) {
      throw new IllegalStateException("비공개 채널은 이름을 수정할 수 없습니다.");
    }

    Optional.ofNullable(req.name()).ifPresent(channelEntity::setChannelName);
    Optional.ofNullable(req.description()).ifPresent(channelEntity::setDescription);

    channelRepository.save(channelEntity);

    Instant lastMessageTime = getLastMessageTime(channelEntity);
    List<UUID> participantIds = getParticipantIds(channelEntity);

    return channelMapper.channelToChannelResponseDto(channelEntity, lastMessageTime,
        participantIds);
  }

  @Override
  public void joinUser(UUID channelId, UUID userId) {
    ChannelEntity channel = isExistedChannel(channelId);
    UserEntity user = isExistedUser(userId);
    channel.addUser(user);
    channelRepository.save(channel);
  }

  @Override
  public void leaveUser(UUID channelId, UUID userId) {
    ChannelEntity channel = isExistedChannel(channelId);
    UserEntity user = isExistedUser(userId);
    channel.removeUser(user);
    channelRepository.save(channel);
  }

  @Override
  public void deleteChannel(UUID channelId) {
    ChannelEntity channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    for (UUID messageId : channel.getMessageIds()) {
      messageRepository.findById(messageId).ifPresent(message -> {
        for (UUID attachmentId : message.getAttachmentIds()) {
          binaryContentRepository.delete(attachmentId);
        }
        messageRepository.delete(message.getId());
      });
    }

    readStatusRepository.delete(channelId);
    channelRepository.delete(channelId);
  }

  private ChannelEntity isExistedChannel(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
  }

  private UserEntity isExistedUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
  }

  private Instant getLastMessageTime(ChannelEntity channel) {
    return channel.getMessageIds().stream()
        .map(messageRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(MessageEntity::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }

  private List<UUID> getParticipantIds(ChannelEntity channel) {
    return channel.getUserIds().stream()
        .map(userRepository::findById)
        .flatMap(Optional::stream)
        .map(UserEntity::getId)
        .collect(Collectors.toList());
  }
}
