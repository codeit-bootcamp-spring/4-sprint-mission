package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.DeleteReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    List<ReadStatusResponseDto> findReadStatusResponsDtoByUserId(UUID userId);
    List<ReadStatusResponseDto> findReadStatusResponseDtoByChannelId(UUID channelId);
    List<ReadStatusResponseDto> findAllReadStatusResponseDto();

    ReadStatusResponseDto findReadStatusResponseDtoByReadStatusId(UUID readStatusId);
    ReadStatusResponseDto updateReadStatus (UUID userId, UUID channelId);
    void deleteReadStatus (DeleteReadStatusRequestDto deleteReadStatusRequestDTO);
    ReadStatusResponseDto createReadStatus(UserResponseDto userResponseDto, ChannelResponseDto channelResponseDto);
}