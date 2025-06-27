package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.DeleteReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto createReadStatus(CreateReadStatusRequestDto createReadStatusRequestDTO);

    List<ReadStatusResponseDto> findReadStatusByUserId (UUID userId);
    List<ReadStatusResponseDto> findReadStatusByChannelId (UUID channelId);
    List<ReadStatusResponseDto> findAllReadStatus();

    ReadStatusResponseDto findReadStatusByReadStatusId (UUID readStatusId);
    ReadStatusResponseDto updateReadStatus (UpdateReadStatusRequestDto updateReadStatusRequestDTO);
    void deleteReadStatus (DeleteReadStatusRequestDto deleteReadStatusRequestDTO);


}
