package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        List<User> users = userRepository.findAll();
        BinaryContent content = null;

        isExistUserByEmail(userCreateDto.getEmail());
        isExistUserByUsername(userCreateDto.getUsername());

        User createdUser = userMapper.UserCreateDtoToUser(userCreateDto);

        if(userCreateDto.getHasProfile()) {
            content = binaryContentRepository.save(binaryContentMapper.userCreateDtoToBinaryContent(userCreateDto));

            createdUser.setProfileId(content.getId());
        }

        UserStatus userStatus = new UserStatus(createdUser.getId());

        userStatusRepository.save(userStatus);

        userRepository.save(createdUser);

        return userMapper.UserToUserResponseDto(createdUser, userStatus.isOnline(), content);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElse(null);
        BinaryContent binaryContent = binaryContentRepository.findById(user.getProfileId()).orElse(null);

        return userMapper.UserToUserResponseDto(user, userStatus.isOnline(), binaryContent);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
            BinaryContent binaryContent = binaryContentRepository.findById(user.getProfileId()).orElse(null);

            return userMapper.UserToUserResponseDto(user, userStatus.isOnline(), binaryContent);
        }).toList();
    }

    @Override
    public UserResponseDto update(UserCreateDto userCreateDto) {
        User user = userRepository.findById(userCreateDto.getId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userCreateDto.getId() + " not found"));
        user.update(userCreateDto);

        BinaryContent content = null;

        if(userCreateDto.getHasProfile()) {
           content = binaryContentRepository.findById(user.getProfileId()).orElse(null);
            if(content != null) {
                binaryContentRepository.deleteById(content.getId());
            }

            content = binaryContentRepository.save(binaryContentMapper.userCreateDtoToBinaryContent(userCreateDto));
            user.setProfileId(content.getId());
        }
        userRepository.save(user);

        return userMapper.UserToUserResponseDto(user, userStatusRepository.findByUserId(user.getId()).get().isOnline(), content);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userStatusRepository.deleteById(userStatusRepository.findByUserId(userId).get().getId());
        binaryContentRepository.deleteById(userRepository.findById(userId).get().getProfileId());
        userRepository.deleteById(userId);
    }

    private void isExistUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }
    private void isExistUserByUsername(String username) {
       Optional<User> user = userRepository.findByUsername(username);
       if(user.isPresent()) {
           throw new IllegalArgumentException("이미 가입된 사용자 이름입니다.");
       }
    }
}
