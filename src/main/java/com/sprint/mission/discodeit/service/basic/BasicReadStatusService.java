package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    @Override
    public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
        validateUser(readStatusCreateRequest);
        validateChannel(readStatusCreateRequest);
        existsByUserAndChannel(readStatusCreateRequest);
        User user = userRepository.findById(readStatusCreateRequest.userId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        Channel channel = channelRepository.findById(readStatusCreateRequest.channelId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
        ReadStatus readStatus = new ReadStatus(user, channel, readStatusCreateRequest.lastReadAt());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        return readStatuses;
    }

    @Transactional
    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus findReadStatus = readStatusRepository.findById(id).orElseThrow(() -> new BusinessLogicException(ExceptionCode.READSTATUS_NOT_FOUND));

        Optional.ofNullable(readStatusUpdateRequest.newLastReadAt()).ifPresent(findReadStatus::setLastReadAt);
        readStatusRepository.save(findReadStatus);
        return findReadStatus;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }

    private void validateUser(ReadStatusCreateRequest readStatusCreateDto) {
        if (!userRepository.existsById(readStatusCreateDto.userId()))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }

    private void validateChannel(ReadStatusCreateRequest readStatusCreateDto) {
        if (!channelRepository.existsById(readStatusCreateDto.channelId()))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);

    }

    private void existsByUserAndChannel(ReadStatusCreateRequest readStatusCreateDto) {
        if (readStatusRepository.existsByUser_IdAndChannel_Id(readStatusCreateDto.userId(), readStatusCreateDto.channelId()))
            throw new BusinessLogicException(ExceptionCode.READSTATUS_ALREADY_EXISTS);
    }

}
