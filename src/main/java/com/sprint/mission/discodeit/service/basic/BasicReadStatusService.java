package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus_dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.DeleteReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto createReadStatus(CreateReadStatusRequestDto createReadStatusRequestDTO) {

        // 원래는 유저에서 했던 기능인데 가져옴
        UUID userId = createReadStatusRequestDTO.getUserResponseDto().getUserId();
        UUID channelId = createReadStatusRequestDTO.getChannelResponseDto().getChannelId();
        User user = findUserByUserId(userId);

        // 2. 채널이 존재하지 않으면 예외 발생
        Channel channel = findChannelByChannelId(channelId);
        ReadStatus readStatus = findReadStatusByUserIdAndChannelId(userId, channelId);

        ReadStatus newReadStatus = new ReadStatus(channelId, userId); // 생성자에 messageId 반영
        readStatusRepository.createReadStatus(newReadStatus);

        return new ReadStatusResponseDto(newReadStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findReadStatusByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findReadStatusesByUserId(userId);
        List<ReadStatusResponseDto> readStatusResponseDtos = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            readStatusResponseDtos.add(new ReadStatusResponseDto(readStatus));
        }

        return readStatusResponseDtos;
    }

    @Override
    public List<ReadStatusResponseDto> findReadStatusByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = readStatusRepository.findReadStatusesByChannelId(channelId);
        List<ReadStatusResponseDto> readStatusResponseDtos = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            readStatusResponseDtos.add(new ReadStatusResponseDto(readStatus));
        }
        return readStatusResponseDtos;
    }

    @Override
    public ReadStatusResponseDto findReadStatusByReadStatusId(UUID readStatusId) {

        ReadStatus readStatus = findReadStatusByReadStatusIdByPrivate(readStatusId);
        return new ReadStatusResponseDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllReadStatus() {
        List<ReadStatus> readStatuses = readStatusRepository.loadReadStatuses();
        List<ReadStatusResponseDto> readStatusResponseDtos = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            readStatusResponseDtos.add(new ReadStatusResponseDto(readStatus));
        }
        return readStatusResponseDtos;
    }

    @Override
    public ReadStatusResponseDto updateReadStatus(UpdateReadStatusRequestDto updateReadStatusRequestDTO) {
        ReadStatus readStatus = findReadStatusByReadStatusIdByPrivate(updateReadStatusRequestDTO.getReadStatusId());

        if (readStatus == null) {
            throw new IllegalStateException("존재하지 않는 readStatus입니다.");
        }

        if (!readStatus.getUserId().equals(updateReadStatusRequestDTO.getUserId())) {
            throw new IllegalArgumentException("유저 id와 readStatus id가 불일치 합니다.");
        }
        if (!readStatus.getChannelId().equals(updateReadStatusRequestDTO.getChannelId())) {
            throw new IllegalArgumentException("채널 id와 readStatus id가 불일치 합니다.");
        }

        readStatus.updateUpdatedAt();// 시간만 업데이트
        readStatusRepository.updateReadStatus(readStatus);

        return new ReadStatusResponseDto(readStatus);
    }

    @Override
    public void deleteReadStatus(DeleteReadStatusRequestDto deleteReadStatusRequestDTO) {

        ReadStatus readStatus = findReadStatusByReadStatusIdByPrivate(deleteReadStatusRequestDTO.getReadStatusId());
        if (!readStatus.getUserId().equals(deleteReadStatusRequestDTO.getUserId())) {
            throw new IllegalArgumentException("유저 id와 readStatus id가 불일치 합니다.");
        }

        if (!readStatus.getChannelId().equals(deleteReadStatusRequestDTO.getChannelId())) {
            throw new IllegalArgumentException("채널 id와 readStatus id가 불일치 합니다.");
        }
        readStatusRepository.deleteReadStatusByReadStatusId(deleteReadStatusRequestDTO.getReadStatusId());
    }

    private User findUserByUserId(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }

    private Channel findChannelByChannelId(UUID channelId) {
        return channelRepository.findChannelByChannelId(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다."));
    }

    private ReadStatus findReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findReadStatusesByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 ReadStatus를 찾을 수 없습니다."));
    }

    private ReadStatus findReadStatusByReadStatusIdByPrivate(UUID readStatusId) {
        return readStatusRepository.findReadStatusesByReadStatusId(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 ReadStatus를 찾을 수 없습니다."));
    }


}