package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadStatusMapper {
    /**
     * ReadStatusCreateDto → ReadStatus 엔티티 변환
     */
    public ReadStatus toEntity(ReadStatusCreateDto dto) {
        return new ReadStatus(
                dto.getUserId(),
                dto.getChannelId()
        );
    }

    /**
     * ReadStatusUpdateDto → 기존 ReadStatus 엔티티 덮어쓰기
     */
    public void updateEntity(ReadStatusUpdateDto dto, ReadStatus readStatus) {
        // 사용자가 마지막으로 메시지를 읽은 시간 업데이트
        if (dto.getId() != null) readStatus.touch();
    }

    /**
     * ReadStatus → UserResponseDto 변환
     */
    public ReadStatusResponseDto toDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getLastReadAt(),
                readStatus.getUserId(),
                readStatus.getChannelId()
        );
    }

    /**
     * ReadStatus → ReadStatusResponseDto 리스트로 변환
     */
    public List<ReadStatusResponseDto> toDtoList(List<ReadStatus> readStatuses) {
        return readStatuses.stream()
                .map(this::toDto)
                .toList();
    }

}
