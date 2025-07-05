package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentService binaryContentService;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserResponseDto createUser(UserCreateDto userDto) {
        User user = userMapper.userCreateDtotoUser(userDto);
        validateDuplicateUser(user);
        userRepository.save(user);

        UUID profileId = null;
        if (userDto.binaryContentDto() != null) {
            BinaryContent binaryContent =
                    binaryContentMapper.BinaryContentRequestDtoToBinaryContent(
                            userDto.binaryContentDto(), user.getId());
            binaryContentRepository.save(binaryContent);
            profileId = binaryContent.getId();
        }

        UserStatus userStatus = new UserStatus(user.getId());
        if (userDto.status()) {
            userStatus.updateAccessTime();
        }
        userStatusRepository.save(userStatus);

        boolean status = getUserStatus(user.getId());

        user.setProfileId(profileId);
        return userMapper.userToUserResponseDto(user, status);
    }

    @Override
    public List<UserResponseDto> findAllUser() {
        return userRepository.findAll().stream()
                .map(
                        user -> {
                            UUID profileId =
                                    binaryContentRepository.findAll().stream()
                                            .filter(b -> b.getUserId().equals(user.getId()))
                                            .map(BinaryContent::getId)
                                            .findFirst()
                                            .orElse(null);
                            boolean status = getUserStatus(user.getId());
                            user.setProfileId(profileId);
                            return userMapper.userToUserResponseDto(user, status);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findUserById(UUID userId) {
        User user = isExistUser(userId);
        UUID profileId =
                binaryContentRepository.findAll().stream()
                        .filter(b -> b.getUserId().equals(userId))
                        .map(BinaryContent::getId)
                        .findFirst()
                        .orElse(null);
        boolean status = getUserStatus(userId);
        user.setProfileId(profileId);
        return userMapper.userToUserResponseDto(user, status);
    }

    @Override
    public List<UserResponseDto> findUsersByNameContains(String userName) {
        return userRepository.findUsersByNameContains(userName).stream()
                .map(
                        user -> {
                            UUID profileId =
                                    binaryContentRepository.findAll().stream()
                                            .filter(b -> b.getUserId().equals(user.getId()))
                                            .map(BinaryContent::getId)
                                            .findFirst()
                                            .orElse(null);
                            boolean status = getUserStatus(user.getId());
                            user.setProfileId(profileId);
                            return userMapper.userToUserResponseDto(user, status);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto) {
        User findUser = isExistUser(userUpdateDto.id());
        Optional.ofNullable(userUpdateDto.name()).ifPresent(findUser::setUserName);

        UUID profileId = null;

        if (userUpdateDto.image() != null) {
            BinaryContent profile =
                    binaryContentRepository.findAll().stream()
                            .filter(binaryContent -> binaryContent.getUserId().equals(userUpdateDto.id()))
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "ID: %s에 대한 프로필 이미지가 존재하지 않습니다.".formatted(userUpdateDto.id())));
            profile.setData(userUpdateDto.image().data());
            binaryContentRepository.save(profile);
            profileId = profile.getId();
        }

        userRepository.save(findUser);
        boolean status = getUserStatus(userUpdateDto.id());

        findUser.setProfileId(profileId);
        return userMapper.userToUserResponseDto(findUser, status);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = isExistUser(userId);

        for (Channel channel : channelRepository.findAll()) {
            if (channel.getUserIds().contains(userId)) {
                channel.getUserIds().remove(userId);

                if (channel.getUserIds().isEmpty() && channel.getType() == ChannelType.PRIVATE) {
                    channelRepository.delete(channel.getId());
                } else {
                    channelRepository.save(channel);
                }
            }
        }

        List<BinaryContent> binaryContents =
                binaryContentRepository.findAll().stream()
                        .filter(bc -> bc.getUserId().equals(userId))
                        .toList();
        for (BinaryContent bc : binaryContents) {
            binaryContentRepository.delete(bc.getId());
        }

        userStatusRepository.findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst()
                .ifPresent(us -> userStatusRepository.delete(us.getId()));

        userRepository.delete(userId);
    }

    @Override
    public List<MessageResponseDto> findMessagesByUser(UUID userId) {
        isExistUser(userId);

        return messageRepository.findAll().stream()
                .filter(msg -> msg.getAuthorId().equals(userId))
                .filter(msg -> msg.getState() != MessageState.DELETED)
                .map(messageMapper::messageToMessageResponseDto)
                .collect(Collectors.toList());
    }

    private User isExistUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

    private void validateDuplicateUser(User user) {
        validateDuplicateUserName(user);
        validateDuplicateUserEmail(user);
    }

    private void validateDuplicateUserName(User user) {
        boolean valid =
                userRepository.findAll().stream().anyMatch(u -> u.getUserName().equals(user.getUserName()));
        if (valid) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
    }

    private void validateDuplicateUserEmail(User user) {
        boolean valid =
                userRepository.findAll().stream()
                        .anyMatch(u -> u.getUserEmail().equals(user.getUserEmail()));
        if (valid) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이메일입니다.");
        }
    }

    private boolean getUserStatus(UUID userId) {
        return userStatusRepository.findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst()
                .map(UserStatus::isOnline)
                .orElse(false);
    }

    private byte[] getUserImage(UUID userId) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> binaryContent.getUserId().equals(userId))
                .map(BinaryContent::getData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
    }
}
