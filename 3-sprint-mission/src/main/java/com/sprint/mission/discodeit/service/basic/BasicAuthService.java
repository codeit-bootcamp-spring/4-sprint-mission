package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.login.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDto login(LoginRequestDto loginRequestDto) {
        User loginUser =
                userRepository.findAll().stream()
                        .filter(
                                user ->
                                        user.getUserName().equals(loginRequestDto.userName())
                                                && user.getUserPassword().equals(loginRequestDto.password()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("사용자 이름 또는 비밀번호가 일치하지 않습니다."));

        userStatusRepository
                .findById(loginUser.getId())
                .ifPresent(
                        userStatus -> {
                            userStatus.updateAccessTime();
                            userStatusRepository.save(userStatus); // 변경 사항 저장
                        });

        byte[] image =
                binaryContentRepository
                        .findById(loginUser.getId())
                        .map(BinaryContent::getData)
                        .orElse(null);

        boolean status =
                userStatusRepository.findById(loginUser.getId()).map(UserStatus::isOnline).orElse(false);

        return userMapper.userToUserResponseDto(loginUser, status, image);
    }
}
