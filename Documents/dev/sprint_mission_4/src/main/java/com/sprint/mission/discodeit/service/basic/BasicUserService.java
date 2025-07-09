package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;


    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 아이디 " + userId + "에 해당하는 유저를 찾지 못했습니다."));
        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    @Override
    public UserResponseDto update(UserUpdateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("유저 아이디 " + dto.getUserId() + "에 해당하는 유저를 찾지 못했습니다."));
        //프로필 이미지 교체 여부 확인(기존에 프로필 이미지가 등록되어 있는지 확인)
        if (dto.getBinaryContent() != null) {
            //기존 이미지 삭제
            if (user.getProfileImageId() != null) {
                binaryContentRepository.deleteById(user.getProfileImageId());
            }
            //새로운 프로필 이미지 저장
            UUID newProfileImageId = saveProfileImage(dto.getBinaryContent(), user.getId()).getId();
            user.setProfileImageId(newProfileImageId);
        }
        //사용자 정보 업데이트
        user.update(dto.getUsername(),dto.getEmail(), dto.getPassword(), dto.getProfileImageId());
        User updatedUser = userRepository.save(user);
        return userMapper.userToUserResponseDto(updatedUser);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 아이디 " + userId + "에 해당하는 유저를 찾지 못했습니다."));
        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        // 중복 체크
        if (userRepository.existsByUserName(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 1. 프로필 이미지 먼저 저장 (nullable)
        UUID profileImageId = null;
        if (dto.getBinaryContent() != null) {
            profileImageId = saveProfileImage(dto.getBinaryContent(), null).getId(); // userId는 아직 없으니 null
        }

        // 2. 유저 엔티티 생성
        User user = userMapper.userCreateDtoToUser(dto);

        // 3. 이미지 ID 세팅
        user.setProfileImageId(profileImageId);

        // 4. 유저 저장
        User savedUser = userRepository.save(user);

        // 5. 프로필 이미지가 있다면 userId 세팅하고 다시 저장
        if (profileImageId != null) {
            BinaryContent binaryContent = binaryContentRepository.findById(profileImageId)
                    .orElseThrow(() -> new IllegalStateException("저장된 프로필 이미지가 조회되지 않았습니다."));
            binaryContent.setUserId(savedUser.getId());
            binaryContentRepository.save(binaryContent);
        }

        // 6. 유저 상태 저장
        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStatusRepository.save(userStatus);

        return userMapper.userToUserResponseDto(savedUser);
    }

    private BinaryContent saveProfileImage(BinaryContentCreateDto binaryContentCreateDto, UUID userId) {
        BinaryContent createdBinaryContent =
                binaryContentMapper.binaryContentCreateDtoToBinaryContent(binaryContentCreateDto);
        createdBinaryContent.setUserId(userId);
        return binaryContentRepository.save(createdBinaryContent);
    }
}



