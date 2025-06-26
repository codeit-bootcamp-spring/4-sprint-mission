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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusResponseDto createReadStatus(ReadStatusCreateDto readStatusCreateDto) {
        if (readStatusCreateDto.channelId() == null || readStatusCreateDto.userId() == null) {
            throw new IllegalArgumentException("user와 channel은 필수입니다.");
        }

        userRepository
                .findById(readStatusCreateDto.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 user는 존재하지 않습니다."));
        channelRepository
                .findById(readStatusCreateDto.channelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 channel은 존재하지 않습니다."));

        boolean exists =
                readStatusRepository.findAll().stream()
                        .anyMatch(
                                rs ->
                                        rs.getUserId().equals(readStatusCreateDto.userId())
                                                && rs.getChannelId().equals(readStatusCreateDto.channelId()));
        if (exists) {
            throw new IllegalArgumentException("이미 해당 채널에 읽음 상태가 존재합니다.");
        }

        ReadStatus readStatus = readStatusMapper.readStatusCreateDtoToReadStatus(readStatusCreateDto);

        return readStatusMapper.toReadStatusResponse(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusResponseDto findReadStatus(UUID readStatusId) {
        ReadStatus readStatus =
                readStatusRepository
                        .findById(readStatusId)
                        .orElseThrow(() -> new IllegalArgumentException("readStatus가 존재하지 않습니다."));
        return readStatusMapper.toReadStatusResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllReadStatus(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .map(readStatusMapper::toReadStatusResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponseDto updateReadStatus(ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatus readStatus =
                readStatusRepository
                        .findById(readStatusUpdateDto.id())
                        .orElseThrow(() -> new IllegalArgumentException("ReadStatus를 찾지 못했습니다."));
        readStatus.updateReadTime();
        readStatusRepository.save(readStatus);
        return readStatusMapper.toReadStatusResponse(readStatus);
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        readStatusRepository
                .findById(readStatusId)
                .ifPresentOrElse(
                        binaryContent -> {
                            readStatusRepository.delete(readStatusId);
                        },
                        () -> {
                            throw new IllegalArgumentException("삭제할 binaryContent가 없습니다.");
                        });
    }
}
