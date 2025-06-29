package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserStatusRequest;
import com.sprint.mission.discodeit.DTO.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    private UserStatusResponse toDto(UserStatus status) {
        return new UserStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getCreatedAt(),
                status.getUpdatedAt(),
                status.getLastSeenAt()
        );
    }

    @Override
    public UserStatusResponse create(UserStatusRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found with id: " + request.userId());
        }
        if (userStatusRepository.existsByUserId(request.userId())) {
            throw new IllegalStateException("UserStatus already exists for userId: " + request.userId());
        }

        UserStatus userStatus = new UserStatus(request);
        userStatusRepository.save(userStatus);
        return toDto(userStatus);
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        return userStatusRepository.findAll().stream()
                .filter(us -> us.getId().equals(id))
                .findFirst()
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusRequest request) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(us -> us.getId().equals(request.id()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + request.id() + " not found"));

        userStatus.updateLastSeenAt(request.lastSeenAt());
        userStatusRepository.save(userStatus);
        return toDto(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UserStatusRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus for userId " + request.userId() + " not found"));
        userStatus.updateLastSeenAt(request.lastSeenAt());
        userStatusRepository.save(userStatus);
        return toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(us -> us.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
        userStatusRepository.deleteByUserId(userStatus.getUserId());
    }
}
