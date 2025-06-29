package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthRequestDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.LoginType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final FileBinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto login(AuthRequestDto authRequestDto) {
        User user = userRepository.findByEmail(authRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다."));

        userRepository.passwordMatches(user, authRequestDto.getPassword());

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
        userStatus.setLoginType(LoginType.ONLINE);

        userStatusRepository.save(userStatus);

        return userMapper.UserToUserResponseDto(user, true, binaryContentRepository.findById(user.getProfileId()).orElse(null));
    }

    @Override
    public void logout(UUID uuid) {
        UserStatus userStatus = userStatusRepository.findByUserId(uuid).orElse(null);
        userStatus.setLoginType(LoginType.OFFLINE);

        userStatusRepository.save(userStatus);
    }
}
