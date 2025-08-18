package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateDto dto) {
        if (!channelRepository.existsById(dto.getChannelId()) || !userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("해당 채널 데이터가 존재하지 않습니다.");
        }
        if(readStatusRepository.existsByChannelIdAndUserId(dto.getChannelId(), dto.getUserId())) {
            throw new IllegalArgumentException("이미 ReadStatus가 존재합니다.");
        }
        User user = userRepository.findById(dto.getUserId()).orElseThrow(NoSuchElementException::new);
        Channel channel = channelRepository.findById(dto.getChannelId()).orElseThrow(NoSuchElementException::new);
        ReadStatus readStatus = readStatusMapper.toEntity(user, channel,dto.getLastReadAt());
        return readStatusMapper.readStatusToReadStatusDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));
        return readStatusMapper.readStatusToReadStatusDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
            return readStatuses.stream()
                    .map(readStatusMapper::readStatusToReadStatusDto)
                    .toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(ReadStatusUpdateDto dto) {
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(dto.getUserId(), dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("해당 데이터가 존재하지 않습니다."));

        readStatus.setLastReadAt(dto.getReadAt());
        return readStatusMapper.readStatusToReadStatusDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 대상이 존재하지 않습니다."));
        readStatusRepository.delete(readStatus);
    }
}
