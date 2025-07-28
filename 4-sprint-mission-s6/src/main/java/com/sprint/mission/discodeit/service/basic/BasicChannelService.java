package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  //
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;
  private final UserRepository userRepository;


  //공개 채널
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    // 1) 엔티티 생성
    Channel channel = channelMapper.fromPublicCreateRequest(request);

    // 2) 저장
    Channel saved = channelRepository.save(channel);

    // 3) DTO로 변환 후 반환
    return channelMapper.toDto(saved, messageRepository, readStatusRepository);
  }

  //비공개 채널
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = channelMapper.fromPrivateCreateRequest(request);
    Channel saved = channelRepository.save(channel);

    // 2) participantIds 로 User 를 한 번에 조회
    List<User> users = userRepository.findAllById(request.participantIds());
    if (users.size() != request.participantIds().size()) {
      throw new EntityNotFoundException("존재하지 않는 사용자 ID가 포함되어 있습니다.");
    }

    // 3) Stream.toList() + saveAll 한 줄로 ReadStatus 생성/저장
    Instant initialReadAt = saved.getCreatedAt();
    readStatusRepository.saveAll(
            users.stream()
                    .map(user -> new ReadStatus(user, saved, initialReadAt))
                    .toList()
    );

    // 4) Entity → DTO 변환
    return channelMapper.toDto(
            saved,
            messageRepository,
            readStatusRepository
    );
  }

  @Override
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found"));

    return channelMapper.toDto(channel, messageRepository, readStatusRepository);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
            .toList();

    List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

    List<Channel> privateChannels = channelRepository.findAllById(privateChannelIds);

    List<Channel> allAccessibleChannels = new ArrayList<>();
    allAccessibleChannels.addAll(publicChannels);
    allAccessibleChannels.addAll(privateChannels);

    return allAccessibleChannels.stream()
            .map(channel -> channelMapper.toDto(channel, messageRepository,readStatusRepository))
            .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() ->
                    new NoSuchElementException("Channel with id " + channelId + " not found")
            );

    // 2) 타입 검사
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    // 3) Mapper 로부터 필드 복사 (null 인 필드는 무시)
    channelMapper.updateFromPublicUpdateRequest(request, channel);

    // 4) 저장
    Channel saved = channelRepository.save(channel);

    // 5) DTO 변환 후 반환
    return channelMapper.toDto(
            saved,
            messageRepository,
            readStatusRepository
    );
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    channelRepository.deleteById(channelId);
  }
}
