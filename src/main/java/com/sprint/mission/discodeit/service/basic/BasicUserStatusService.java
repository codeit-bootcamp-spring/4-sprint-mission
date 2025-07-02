package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusResponseDto create(UserStatusCreateDto dto) {

        User user = getUserOrThrow(dto.getUserId());
        /*Optional<UserStatus> existing = userStatusRepository.findByUserId(dto.getUserId());
        if (existing.isPresent()) {
            throw new IllegalAccessException("이미 존재하는 userStatus임");
        }*/
        UserStatus userStatus = userStatusMapper.toEntity(dto);
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(user, userStatus);
    }

    // 중복 메서드 처리
    private User getUserOrThrow(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }
}
