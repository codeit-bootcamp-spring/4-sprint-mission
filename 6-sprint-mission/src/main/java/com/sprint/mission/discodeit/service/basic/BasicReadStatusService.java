package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusDto createReadStatus(ReadStatusCreateRequest readStatusCreateRequest) {
        UUID userId = readStatusCreateRequest.userId();
        UUID channelId = readStatusCreateRequest.channelId();

        if (userId == null || channelId == null) {
            throw new IllegalArgumentException("user와 channel은 필수입니다.");
        }

        User user =
                userRepository
                        .findById(readStatusCreateRequest.userId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 user는 존재하지 않습니다."));
        Channel channel =
                channelRepository
                        .findById(readStatusCreateRequest.channelId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 channel은 존재하지 않습니다."));

        isExistsReadStatus(readStatusCreateRequest);

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatus.setUser(user);
        readStatus.setChannel(channel);

        return readStatusMapper.readStatusToReadStatusDto(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusDto findReadStatus(UUID readStatusId) {
        ReadStatus readStatus =
                readStatusRepository
                        .findById(readStatusId)
                        .orElseThrow(() -> new IllegalArgumentException("readStatus가 존재하지 않습니다."));
        return readStatusMapper.readStatusToReadStatusDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllReadStatusByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::readStatusToReadStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatusDto> findAllReadStatusByChannelId(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId).stream()
                .map(readStatusMapper::readStatusToReadStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusDto updateReadStatus(
            UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus =
                readStatusRepository
                        .findById(readStatusId)
                        .orElseThrow(() -> new IllegalArgumentException("ReadStatus를 찾지 못했습니다."));

        readStatus.updateReadTime(readStatusUpdateRequest.newLastReadAt());

        return readStatusMapper.readStatusToReadStatusDto(readStatus);
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        ReadStatus readStatus =
                readStatusRepository
                        .findById(readStatusId)
                        .orElseThrow(() -> new IllegalArgumentException("삭제할 readStatus가 없습니다."));
        readStatusRepository.delete(readStatus);
    }

    private void isExistsReadStatus(ReadStatusCreateRequest readStatusCreateRequest) {
        boolean exists =
                !readStatusRepository
                        .findAllByChannelIdAndUserId(
                                readStatusCreateRequest.channelId(), readStatusCreateRequest.userId())
                        .isEmpty();
        if (exists) {
            throw new IllegalArgumentException("이미 해당 채널에 읽음 상태가 존재합니다.");
        }
    }
}
