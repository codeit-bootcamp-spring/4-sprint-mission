package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
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
    public ReadStatusResponseDto find(UUID id) {
        ReadStatus status = getReadStatusOrThrow(id);

        return ReadStatusMapper.entityToDto(status);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto updateDto) {
        ReadStatus readStatus = getReadStatusOrThrow(updateDto.getId());
        readStatus.updateReadTime();

        return ReadStatusMapper.entityToDto(readStatusRepository.save(readStatus));
    }

    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus not found : " + id);
        }

        readStatusRepository.deleteById(id);
    }

    private ReadStatus getReadStatusOrThrow(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found : " + id));
    }

}
