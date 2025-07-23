package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateRequest readStatusCreateRequest) {
        validateUser(readStatusCreateRequest);
        validateChannel(readStatusCreateRequest);
        existsByUserAndChannel(readStatusCreateRequest);

        ReadStatus readStatus = new ReadStatus(readStatusCreateRequest.userId(), readStatusCreateRequest.channelId(), Instant.now());
        readStatusRepository.save(readStatus);
        ReadStatusResponseDto readStatusResponseDto = readStatusMapper.toReadStatusResponseDto(readStatus);
        return readStatusResponseDto;
    }

    @Override
    public ReadStatusResponseDto find(UUID id) {
        return readStatusMapper.toReadStatusResponseDto(readStatusRepository.findById(id));
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatusResponseDto> readStatusResponseDtos = new ArrayList<>();
        readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .forEach(readStatus -> readStatusResponseDtos.add(readStatusMapper.toReadStatusResponseDto(readStatus)));
        return readStatusResponseDtos;
    }

    @Override
    public ReadStatusResponseDto update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus findReadStatus = readStatusRepository.findById(id);

        Optional.ofNullable(readStatusUpdateRequest.newLastReadAt()).ifPresent(findReadStatus::setLastReadAt);
        readStatusRepository.save(findReadStatus);
        return readStatusMapper.toReadStatusResponseDto(findReadStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }

    // 테스트용 findAll()
    public List<ReadStatusResponseDto> findAll() {
        List<ReadStatusResponseDto> readStatusResponseDtos = new ArrayList<>();
        readStatusRepository.findAll().stream()
                .forEach(readStatus -> readStatusResponseDtos.add(readStatusMapper.toReadStatusResponseDto(readStatus)));
        return readStatusResponseDtos;
    }

    private void validateUser(ReadStatusCreateRequest readStatusCreateDto) {
        if (userRepository.findAll().stream()
                .noneMatch(user -> user.getId().equals(readStatusCreateDto.userId())))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }

    private void validateChannel(ReadStatusCreateRequest readStatusCreateDto) {
        if (channelRepository.findAll().stream()
                .noneMatch(channel -> channel.getId().equals(readStatusCreateDto.channelId())))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }

    private void existsByUserAndChannel(ReadStatusCreateRequest readStatusCreateDto) {
        if (readStatusRepository.findAll().stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(readStatusCreateDto.userId())
                        && readStatus.getChannelId().equals(readStatusCreateDto.channelId())))
            throw new BusinessLogicException(ExceptionCode.READSTATUS_ALREADY_EXISTS);
    }
}
