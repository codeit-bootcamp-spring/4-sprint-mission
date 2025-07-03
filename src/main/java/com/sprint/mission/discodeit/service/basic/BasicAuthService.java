package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusService userStatusService;

    @Override
    public UserResponseDto logInUser(LoginRequestDto loginRequest) {

        User user = findUserByUserName(loginRequest.getUserName());


        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("일치하지 않은 비밀번호 입니다.");
        }

        BinaryContent currentUserPicture = findBinaryContentByBinaryContentId(user.getProfileId());
        // DTO 반환
        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                currentUserPicture != null ? currentUserPicture.getBinaryContentPath() : null
        );
    }

    private User findUserByUserName(String userName) {
        return userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }

    private BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId) {
        return binaryContentRepository.findBinaryContentByBinaryContentId(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 바이너리 컨텐츠를 찾을 수 없습니다."));
    }
}
