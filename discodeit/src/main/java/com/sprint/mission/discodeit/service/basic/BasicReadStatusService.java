package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("User not found: " + dto.getUserId());
        }
        if (!channelRepository.existsById(dto.getChannelId())) {
            throw new NoSuchElementException("Channel not found: " + dto.getChannelId());
        }

        boolean exists = readStatusRepository.findAllByUserId(dto.getUserId()).stream()
                .anyMatch(status -> status.getChannelId().equals(dto.getChannelId()));
        if (exists) {
            throw new IllegalArgumentException("ReadStatus already exists for user and channel");
        }

        ReadStatus save = readStatusRepository.save(new ReadStatus(dto.getUserId(), dto.getChannelId()));

        return ReadStatusMapper.entityToDto(save);
    }

    @Override
    public ReadStatusResponseDto find(UUID channelId, UUID userId) {
        ReadStatus status = getReadStatusOrThrow(channelId, userId);

        return ReadStatusMapper.entityToDto(status);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatus> list = readStatusRepository.findAllByUserId(userId);
        return list.stream().map(ReadStatusMapper::entityToDto).toList();
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusDto updateDto) {
        ReadStatus readStatus = getReadStatusOrThrow(updateDto.getChannelId(), updateDto.getUserId());
        readStatus.updateReadTime();

        return ReadStatusMapper.entityToDto(readStatusRepository.save(readStatus));
    }

    @Override
    public void delete(UUID channelId, UUID userId) {
        if (!readStatusRepository.existsById(channelId, userId)) {
            throw new NoSuchElementException("ReadStatus not found : channelId=" + channelId + ", userId=" + userId);
        }

        readStatusRepository.deleteByChannelIdAndUserId(channelId, userId);
    }

    private ReadStatus getReadStatusOrThrow(UUID channelId, UUID userId) {
        return readStatusRepository.findByChannelIdAndUserId(channelId,userId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found: channelId=" + channelId + ", userId=" + userId));
    }

}
