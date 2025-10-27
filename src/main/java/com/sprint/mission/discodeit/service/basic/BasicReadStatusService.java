package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.constant.ReadStatusErrorCode;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateServiceRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    private final ChannelRepository channelRepository;

    private final UserRepository userRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusResponse createReadStatus(ReadStatusCreateServiceRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        readStatusRepository.findReadStatusByUserIdAndChannelId(request.getUserId(), request.getChannelId())
                .ifPresent(readStatus -> {throw new ReadStatusException(ReadStatusErrorCode.READ_STATUS_ALREADY_EXIST);});

        ReadStatus readStatus = readStatusMapper.toEntity(request, user, channel);

        readStatusRepository.save(readStatus);

        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusResponse findReadStatusById(UUID userId) {
        return readStatusRepository.findById(userId)
                .map(readStatusMapper::toResponse)
                .orElseThrow(() -> new ReadStatusException(ReadStatusErrorCode.READ_STATUS_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusResponse findReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findReadStatusByUserIdAndChannelId(userId, channelId)
                .map(readStatusMapper::toResponse)
                .orElseThrow(() -> new ReadStatusException(ReadStatusErrorCode.READ_STATUS_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(readStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusResponse updateReadStatus(ReadStatusUpdateServiceRequest request) {

        ReadStatus readStatusToUpdate = readStatusRepository.findById(request.getReadStatusId())
                .orElseThrow(() -> new ReadStatusException(ReadStatusErrorCode.READ_STATUS_NOT_FOUND));

        readStatusToUpdate.updateLastReadAt();

        readStatusRepository.save(readStatusToUpdate);

        return readStatusMapper.toResponse(readStatusToUpdate);

    }

    @Override
    @Transactional
    public void deleteReadStatus(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ReadStatusException(ReadStatusErrorCode.READ_STATUS_NOT_FOUND));

        Channel channel = channelRepository.findById(readStatus.getChannelId())
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

        User user = userRepository.findById(readStatus.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        channelRepository.save(channel);
        userRepository.save(user);
        readStatusRepository.deleteById(readStatusId);
    }

}
