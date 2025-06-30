package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserStatusMapper {
    /**
     * UserStatusCreateDto → UserStatus 엔티티 변환
     */
    public UserStatus toEntity(UserStatusCreateDto dto) {
        return new UserStatus(dto.getUserId());
    }

    /**
     * UserStatusUpdateDto → 기존 UserStatus 엔티티 덮어쓰기
     */
    public void updateEntity(UserStatusUpdateDto dto, UserStatus userStatus) {
        if (dto.getId() != null) {
            // 사용자가 마지막으로 확인된 접속 시간 업데이트
            userStatus.touch();
            userStatus.updateLastConnectedAt();
        }
    }

    /**
     * UserStatus →  UserStatusResponseDto 변환
     */
    public UserStatusResponseDto toDto(UserStatus userStatus) {
        return new UserStatusResponseDto(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastConnectedAt()
        );
    }

    /**
     * UserStatus → UserStatusResponseDto 리스트로 변환
     */
    public List<UserStatusResponseDto> toDtoList(List<UserStatus> userStatuses) {
        return userStatuses.stream()
                .map(this::toDto)
                .toList();
    }

}
