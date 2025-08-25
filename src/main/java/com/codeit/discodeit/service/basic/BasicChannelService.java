package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.channel.ChannelNameDuplicateException;
import com.codeit.discodeit.exception.channel.ChannelNotFoundException;
import com.codeit.discodeit.exception.channel.NoParticipantsChannelException;
import com.codeit.discodeit.exception.user.UserNotFoundException;
import com.codeit.discodeit.mapper.ChannelMapper;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusService readStatusService;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto createPublicChannel(CreatePublicChannelRequestDto createPublicChannelRequestDto) {
    log.info("[createPublicChannel] 요청 수신");

    validateChannelNameDuplicated(createPublicChannelRequestDto.getName());

    Channel channel = channelMapper.toPublicChannel(createPublicChannelRequestDto);
    channelRepository.save(channel);

    List<User> users = userRepository.findAll();
    for (User user : users) {
      readStatusService.createReadStatus(user, channel);
    }

    ChannelDto result = toChannelDto(channel);
    log.info("[createPublicChannel] 채널 생성 완료: channelId={}", result.getId());
    return result;
  }

  @Override
  @Transactional
  public ChannelDto createPrivateChannel(List<UUID> userIdList) {
    log.info("[createPrivateChannel] 요청 수신: userIds={}", userIdList);

    if (userIdList == null || userIdList.isEmpty()) {
      log.info("[createPrivateChannel] 참여 유저가 없어서 채널 생성 실패: userIds={}", userIdList);
      Map<String, Object> details = Map.of("이유", "참여 유저 없음");
      throw new NoParticipantsChannelException(details);
    }

    Channel channel = channelMapper.toPrivateChannel();
    channelRepository.save(channel);

    List<User> userList = userRepository.findAllById(userIdList);
    for (User user : userList) {
      readStatusService.createReadStatus(user, channel);
    }

    ChannelDto result = toChannelDto(channel);
    log.info("[createPrivateChannel] 채널 생성 완료: channelId={}", result.getId());
    return result;
  }

  @Override
  @Transactional
  public void deleteChannel(UUID channelId) {
    log.info("[deleteChannel] 요청 수신: channelId={}", channelId);
    Optional<Channel> channel = channelRepository.findById(channelId);
    if (channel.isPresent()) {
      channelRepository.delete(channel.get());
      log.info("[deleteChannel] 채널 삭제 완료: channelId={}", channelId);
    } else {
      log.debug("[deleteChannel] 삭제 실패 - 채널을 찾을 수 없음: channelId={}", channelId);
      Map<String, Object> details = Map.of("이유", "채널 없음");
      throw new ChannelNotFoundException(details);
    }
  }

  @Override
  @Transactional
  public ChannelDto updatePublicChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
    log.info("[updatePublicChannel] 요청 수신: channelId={}, newName={}, newDescription={}",
        channelId, publicChannelUpdateRequest.getNewName(), publicChannelUpdateRequest.getNewDescription());

    Channel channel = findChannelByChannelId(channelId);
    channel.setName(publicChannelUpdateRequest.getNewName());
    channel.setDescription(publicChannelUpdateRequest.getNewDescription());

    channelRepository.save(channel);

    ChannelDto result = toChannelDto(channel);
    log.info("[updatePublicChannel] 채널 수정 완료: channelId={}", result.getId());
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findChannelListByUserId(UUID userId){
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      Map<String, Object> details = Map.of("이유", "유저 없음");
      throw new UserNotFoundException(details);
    }
    List<ReadStatus> readStatusList = readStatusService.findReadStatusesByUserId(userId);
    List<Channel> channelList = readStatusList.stream().map(ReadStatus::getChannel).toList();
    return channelList.stream().map(this::toChannelDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Channel findChannelByChannelId(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> {
          Map<String, Object> details = Map.of("이유", "채널 없음");
          return new ChannelNotFoundException(details);
        });
  }

  private void validateChannelNameDuplicated(String channelName) {
    log.info("[validateChannelNameDuplicated] 채널 이름 중복 확인 시작: channelName={}", channelName);
    Optional<Channel> channel = channelRepository.findByName(channelName);
    if (channel.isPresent()) {
      log.debug("[validateChannelNameDuplicated] 채널 이름 중복 발견: channelName={}", channelName);
      Map<String, Object> details = Map.of("이유", "채널 이름 중복");
      throw new ChannelNameDuplicateException(details);
    }
    log.info("[validateChannelNameDuplicated] 채널 이름 중복 확인 종료: channelName={}", channelName);
  }

  private Optional<Message> findLastMessageInChannel(UUID channelId){
    return messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId);
  }

  private ChannelDto toChannelDto(Channel channel) {
    Optional<Message> lastMessage = findLastMessageInChannel(channel.getId());
    List<ReadStatus> readStatuses = readStatusService.findReadStatusesByChannelId(channel);
    return channelMapper.toChannelDto(channel, lastMessage, readStatuses, userMapper);
  }
}
