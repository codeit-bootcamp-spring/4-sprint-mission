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
    private final BinaryContentMapper binaryContentMapper;
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    @Override
    public UserResponseDto createUser(UserCreateDto userDto) {
        User user = userMapper.userCreateDtotoUser(userDto);

        validateDuplicateUser(user);
        userRepository.save(user);

        if (userDto.binaryContentDto() != null) {
            BinaryContent binaryContent =
                    binaryContentMapper.BinaryContentRequestDtoToBinaryContent(
                            userDto.binaryContentDto(), user.getId());
            binaryContentRepository.save(binaryContent);
        }

        UserStatus userStatus = new UserStatus(user.getId());
        if (userDto.userStatus()) {
            userStatus.updateAccessTime();
        }
        userStatusRepository.save(userStatus);

        return userMapper.userToUserResponseDto(user, true, null);
    }

    @Override
    public List<UserResponseDto> findAllUser() {
        return userRepository.findAll().stream()
                .map(
                        user -> {
                            boolean status =
                                    userStatusRepository
                                            .findById(user.getId())
                                            .map(UserStatus::isOnline)
                                            .orElse(false);

                            byte[] image =
                                    binaryContentRepository
                                            .findById(user.getId())
                                            .map(BinaryContent::getData)
                                            .orElse(null);
                            return userMapper.userToUserResponseDto(user, status, image);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findUserById(UUID userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("user가 존재하지 않습니다."));

        boolean status =
                userStatusRepository.findById(user.getId()).map(UserStatus::isOnline).orElse(false);

        byte[] image =
                binaryContentRepository.findById(user.getId()).map(BinaryContent::getData).orElse(null);

        return userMapper.userToUserResponseDto(user, status, image);
    }

    @Override
    public List<UserResponseDto> findUsersByNameContains(String userName) {
        return userRepository.findUsersByNameContains(userName).stream()
                .map(
                        user -> {
                            boolean status =
                                    userStatusRepository
                                            .findById(user.getId())
                                            .map(UserStatus::isOnline)
                                            .orElse(false);

                            byte[] image =
                                    binaryContentRepository
                                            .findById(user.getId())
                                            .map(BinaryContent::getData)
                                            .orElse(null);

                            return userMapper.userToUserResponseDto(user, status, image);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto) {
        User findUser = isExistUser(userUpdateDto.id());

        Optional.ofNullable(userUpdateDto.name()).ifPresent(findUser::setUserName);

        byte[] updatedImage = null;
        Optional.ofNullable(userUpdateDto.image())
                .ifPresent(
                        image -> {
                            BinaryContent profile =
                                    binaryContentRepository
                                            .findById(userUpdateDto.id())
                                            .orElseThrow(
                                                    () -> new IllegalArgumentException("해당 사용자의 프로필 이미지가 존재하지 않습니다."));
                            profile.setData(image);
                            binaryContentRepository.save(profile);
                        });

        User user = userRepository.save(findUser);
        boolean status =
                userStatusRepository.findById(user.getId()).map(UserStatus::isOnline).orElse(false);

        return userMapper.userToUserResponseDto(user, status, updatedImage);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("삭제할 user가 없습니다."));
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

        List<UUID> binaryToDelete =
                binaryContentRepository.findAll().stream()
                        .filter(
                                binaryContent ->
                                        binaryContent.getUserId() != null && binaryContent.getUserId().equals(userId))
                        .map(BinaryContent::getId)
                        .toList();
        binaryToDelete.forEach(binaryContentRepository::delete);

        userStatusRepository.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .map(UserStatus::getId)
                .forEach(userStatusRepository::delete);

        userRepository.delete(userId);
    }

    @Override
    public List<MessageResponseDto> findMessagesByUser(UUID userId) {
        User findUser = isExistUser(userId);
        return findUser.getMessageIds().stream()
                .map(messageRepository::findById)
                .flatMap(Optional::stream) // Optional -> Stream<Message>
                .filter(message -> message.getState() != MessageState.DELETED)
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
}
