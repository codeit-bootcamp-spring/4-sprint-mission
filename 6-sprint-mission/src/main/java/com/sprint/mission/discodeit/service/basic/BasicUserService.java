package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentService basicBinaryContentService;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public UserDto createUser(UserCreateServiceRequest request) {
        BinaryContent binaryContent = null;
        MultipartFile profileFile = request.profile();
        if (profileFile != null && !profileFile.isEmpty()) {
            String fileName = profileFile.getOriginalFilename();
            String contentType = profileFile.getContentType();
            BinaryContentDto profile =
                    basicBinaryContentService.createBinaryContent(
                            new BinaryContentCreateServiceRequest(null, null, fileName, contentType, profileFile));
            binaryContent =
                    binaryContentMapper.binaryContentDtoToBinaryContent(profile);
        }

        User newUser = userMapper.userCreateServiceRequestToUser(request);
        validateDuplicateUser(newUser);
        newUser.setProfile(binaryContent);

        UserStatus userStatus = new UserStatus(newUser);
        newUser.setStatus(userStatus);

        userRepository.save(newUser);

        return userMapper.userToUserDto(newUser, toBinaryContentDto(binaryContent));
    }

    @Override
    public UserDto updateUser(UUID userId, UserUpdateServiceRequest request) {
        User user = isExistUser(userId);

        MultipartFile profileFile = request.profile();
        if (profileFile != null && !profileFile.isEmpty()) {
            if (user.getProfile() != null) {
                basicBinaryContentService.deleteBinaryContent(user.getProfile().getId());
            }
            String fileName = profileFile.getOriginalFilename();
            String contentType = profileFile.getContentType();
            BinaryContentDto updatedBinaryContentDto =
                    basicBinaryContentService.createBinaryContent(
                            new BinaryContentCreateServiceRequest(user.getId(), null,fileName,contentType, profileFile));
            byte[] decodedBytes = null;
            if (updatedBinaryContentDto.bytes() != null && !updatedBinaryContentDto.bytes().isEmpty()) {
                decodedBytes = Base64.getDecoder().decode(updatedBinaryContentDto.bytes());
            }
            binaryContentStorage.put(updatedBinaryContentDto.id(), decodedBytes);
            BinaryContent binaryContent =
                    binaryContentMapper.binaryContentDtoToBinaryContent(
                            updatedBinaryContentDto);

            user.setProfile(binaryContent);
        }

        if (request.newUsername() != null) {
            user.setUsername(request.newUsername());
        }
        if (request.newEmail() != null) {
            user.setEmail(request.newEmail());
        }
        if (request.newPassword() != null) {
            user.setPassword(request.newPassword());
        }

        if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())) {
            validateDuplicateUsername(user);
        }

        if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
            validateDuplicateEmail(user);
        }

        return userMapper.userToUserDto(user, toBinaryContentDto(user.getProfile()));
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = isExistUser(userId);
        if (user.getProfile() != null) {
            basicBinaryContentService.deleteBinaryContent(user.getProfile().getId());
        }
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> findAllUser() {
        return userRepository.findAll().stream()
            .map(user -> userMapper.userToUserDto(user, toBinaryContentDto(user.getProfile())))
            .collect(Collectors.toList());
    }

    private User isExistUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

    private void validateDuplicateUser(User user) {
        validateDuplicateUsername(user);
        validateDuplicateEmail(user);
    }

    private void validateDuplicateUsername(User user) {
        boolean valid =
                userRepository.findAll().stream()
                        .anyMatch(
                                u -> !u.getId().equals(user.getId()) && u.getUsername().equals(user.getUsername()));
        if (valid) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
    }

    private void validateDuplicateEmail(User user) {
        boolean valid =
                userRepository.findAll().stream()
                        .anyMatch(u -> !u.getId().equals(user.getId()) && u.getEmail().equals(user.getEmail()));
        if (valid) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이메일입니다.");
        }
    }

    private BinaryContentDto toBinaryContentDto(BinaryContent binaryContent) {
        if (binaryContent == null) return null;

        try (InputStream inputStream = binaryContentStorage.get(binaryContent.getId())) {
            byte[] bytes = inputStream.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent, base64);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 로딩 실패", e);
        }
    }
}
