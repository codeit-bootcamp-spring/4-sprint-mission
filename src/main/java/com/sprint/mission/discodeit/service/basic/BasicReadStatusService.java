package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.read_status.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.read_status.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    @Override
    public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
        User user = getValidUser(readStatusCreateRequest);
        Channel channel = getValidChannel(readStatusCreateRequest);
        existsByUserAndChannel(readStatusCreateRequest);

        ReadStatus readStatus = new ReadStatus(user, channel, readStatusCreateRequest.lastReadAt());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        return readStatuses;
    }

    @Transactional
    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus findReadStatus = readStatusRepository.findById(id).orElseThrow(() -> new ReadStatusNotFoundException(ErrorCode.READSTATUS_NOT_FOUND, Map.of("readStatusId", id)));

        Optional.ofNullable(readStatusUpdateRequest.newLastReadAt()).ifPresent(findReadStatus::setLastReadAt);
        readStatusRepository.save(findReadStatus);
        return findReadStatus;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }

    private User getValidUser(ReadStatusCreateRequest readStatusCreateRequest) {
        return userRepository.findById(readStatusCreateRequest.userId())
                .orElseThrow(() -> new ReadStatusNotFoundException(ErrorCode.CHANNEL_OR_USER_NOT_FOUND, Map.of("userId", readStatusCreateRequest.userId())));
    }

    private Channel getValidChannel(ReadStatusCreateRequest readStatusCreateRequest) {
        return channelRepository.findById(readStatusCreateRequest.channelId())
                .orElseThrow(() -> new ReadStatusNotFoundException(ErrorCode.CHANNEL_OR_USER_NOT_FOUND, Map.of("channelId", readStatusCreateRequest.channelId())));

    }

    private void existsByUserAndChannel(ReadStatusCreateRequest readStatusCreateDto) {
        if (readStatusRepository.existsByUser_IdAndChannel_Id(readStatusCreateDto.userId(), readStatusCreateDto.channelId()))
            throw new ReadStatusAlreadyExistsException(ErrorCode.READSTATUS_ALREADY_EXISTS, Map.of("userId", readStatusCreateDto.userId(), "channelId", readStatusCreateDto.channelId()));
    }

}
