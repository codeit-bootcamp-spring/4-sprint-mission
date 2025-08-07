package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 아이디 " + userId + "에 해당하는 유저를 찾지 못했습니다."));
        return userMapper.userToUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UserUpdateRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("유저 아이디 " + dto.getUserId() + "에 해당하는 유저를 찾지 못했습니다."));
        //프로필 이미지 교체 여부 확인(기존에 프로필 이미지가 등록되어 있는지 확인)
        if (dto.getBinaryContent() != null) {
            //기존 이미지 삭제
            if (user.getProfile() != null) {
                binaryContentRepository.deleteById(user.getProfile().getId());
            }
            //새로운 프로필 이미지 저장
            MultipartFile file = dto.getBinaryContent().getFile();
            UUID fileId = UUID.randomUUID();
            try {
                binaryContentStorage.put(fileId, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패", e);
            }
            BinaryContent profile = new BinaryContent();;
            profile.setId(fileId);
            profile.setFileName(file.getOriginalFilename());
            profile.setContentType(file.getContentType());
            profile.setSize(file.getSize());

            user.setProfile(profile);
        }
        //사용자 정보 업데이트
        user.update(dto.getNewUsername(),dto.getNewEmail(), dto.getNewPassword());
        return userMapper.userToUserDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 아이디 " + userId + "에 해당하는 유저를 찾지 못했습니다."));
        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public UserDto create(UserCreateRequest dto) {
        // 중복 체크
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 1. 프로필 이미지 먼저 저장 (nullable)
        BinaryContent profileImage = null;
        UUID fileId = UUID.randomUUID();
        if (dto.getFile() != null) {
            try {
                byte[] bytes = dto.getFile().getBytes();
                binaryContentStorage.put(fileId, bytes);
            } catch (IOException e) {
                throw new RuntimeException("유저 생성 중 프로필 이미지 저장 오류 발생", e);
            }

            BinaryContent profileImageEntity = new BinaryContent();
            profileImageEntity.setId(fileId);
            profileImageEntity.setFileName(dto.getFile().getOriginalFilename());
            profileImageEntity.setContentType(dto.getFile().getContentType());
            profileImageEntity.setSize(dto.getFile().getSize());
            profileImage= binaryContentRepository.save(profileImageEntity);
            }

        // 2. 유저 엔티티 생성
        UUID savedFileId = profileImage != null ? profileImage.getId() : null;
        User user = userMapper.userCreateDtoToUser(dto, savedFileId);
        if (profileImage != null) {
            System.out.println("✅ user.profileImageId BEFORE SAVE = " + user.getProfile().getId());  // ✅ 저장 전 확인
        }

        // 3. 유저 저장
        User savedUser = userRepository.save(user);
        System.out.println("✅ savedUser.profileImageId AFTER SAVE = " + savedUser.getProfile().getId());  // ✅ 저장 후 확인

        return userMapper.userToUserDto(savedUser);
    }
}



