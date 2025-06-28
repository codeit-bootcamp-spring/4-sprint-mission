package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;

    //
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto readStatusCreateDto) {
        // user 유효성 검사
        if (!userRepository.existsById(readStatusCreateDto.userId())) {
            throw new NoSuchElementException(this.getClass().getSimpleName() + ".create() 실패: userId에 해당하는 유저가 존재하지 않습니다.");
        }
        
        // channel 유효성 검사
        if (!channelRepository.existsById(readStatusCreateDto.channelId())) {
            throw new NoSuchElementException(this.getClass().getSimpleName() + ".create() 실패: channelId에 해당하는 채널이 존재하지 않습니다.");
        }
        
        // readStatus 중복 검사
        if (detectDuplicatedReadStatus(readStatusCreateDto)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create() 실패: 중복해서 ReadStatus를 생성할 수 없습니다.");
        }

        ReadStatus readStatus = readStatusMapper.readStatusCreateDtoToReadStatus(readStatusCreateDto);
        readStatusRepository.save(readStatus);
        return readStatusMapper.readStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("BasicReadStatusService.find() 실패: readStatusId is invalid."));

        return readStatusMapper.readStatusToReadStatusResponseDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        return readStatuses.stream().map(readStatusMapper::readStatusToReadStatusResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<ReadStatusResponseDto> findAllByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
        return readStatuses.stream().map(readStatusMapper::readStatusToReadStatusResponseDto).collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatus updatedReadStatus = readStatusRepository.findById(readStatusUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException("BasicReadStatusService.find() 실패: readStatusId is invalid."));

        updatedReadStatus.update(readStatusUpdateDto.newReadTime());
        readStatusRepository.save(updatedReadStatus);
        return readStatusMapper.readStatusToReadStatusResponseDto(updatedReadStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (readStatusRepository.findById(readStatusId).isEmpty()) {
            System.out.println("BasicReadStatusService.delete() 실패: readStatusId is invalid.");
            return;
        }
        readStatusRepository.delete(readStatusId);
    }

    private boolean detectDuplicatedReadStatus(ReadStatusCreateDto readStatusCreateDto) {
        Optional<ReadStatus> findReadStatus = readStatusRepository.findAllByUserId(readStatusCreateDto.userId()).stream()
                .filter(readStatus -> readStatus.getChannelId().equals(readStatusCreateDto.channelId()))
                .findFirst();

        return findReadStatus.isPresent();
    }
}
