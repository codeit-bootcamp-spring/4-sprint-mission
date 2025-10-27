package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.info("유저 단건 조회: userId ={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("유저 조회 실패-존재하지 않는 ID: userId = {}", userId);
                    return new UserNotFoundException(userId);
                });
        return userMapper.userToUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.info("유저 전체 조회 요청");

        List<UserDto> users =  userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .toList();

        log.debug("유저 전체 조회 결과 개수: {}", users.size());
        return users;
    }

    @Transactional
    @Override
    public UserDto update(UserUpdateRequest dto) {
        log.info("유저 정보 수정 요청: userId = {}", dto.getUserId());

        // 유저 정보 존재 여부 확인
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->{
                    log.error("유저 정보 업데이트 실패-존재하지 않는 ID: userId = {}", dto.getUserId());
                    return new UserNotFoundException(dto.getUserId());
                });

        //프로필 이미지 교체 여부 확인(기존에 프로필 이미지가 등록되어 있는지 확인)
        if (dto.getBinaryContent() != null) {
            //기존 이미지 삭제
            if (user.getProfile() != null) {
                log.debug("기존 프로필 이미지 삭제: fileId = {}", user.getProfile().getId());
                binaryContentRepository.deleteById(user.getProfile().getId());
            }
            //새로운 프로필 이미지 저장
            MultipartFile file = dto.getBinaryContent().getFile();
            UUID fileId = UUID.randomUUID();
            try {
                binaryContentStorage.put(fileId, file.getBytes());
            } catch (IOException e) {
                log.error("유저 정보 업데이트 실패-파일 저장 실패: fileName ={}, userId = {}", file.getOriginalFilename(), dto.getUserId(), e);
                throw new RuntimeException("파일 저장 실패", e);
            }
            BinaryContent profile = new BinaryContent();;
            profile.setId(fileId);
            profile.setFileName(file.getOriginalFilename());
            profile.setContentType(file.getContentType());
            profile.setSize(file.getSize());

            user.setProfile(profile);
            log.info("새로운 프로필 이미지 등록 성공: fileId = {}", fileId);
        }
        //사용자 정보 업데이트
        user.update(dto.getNewUsername(),dto.getNewEmail(), dto.getNewPassword());
        log.info("유저 정보 업데이트 완료: userId = {}", dto.getUserId());
        return userMapper.userToUserDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        //유저 정보 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(" 유저 정보 삭제 실패-존재하지 않는 ID: userId = {}", userId);
                    return new UserNotFoundException(userId);
                });
        if (user.getProfile() != null) {
                log.debug("프로필 이미지 삭제: fileId = {}", user.getProfile().getId());
            binaryContentRepository.deleteById(user.getProfile().getId());
        }
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);
        log.info("유저 정보 삭제 완료: userId = {}", userId);
    }

    @Transactional
    @Override
    public UserDto create(UserCreateRequest dto) {
        log.info("유저 생성 요청: username={}, email={}", dto.getUsername(), dto.getEmail());

        // 중복 체크
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("유저 생성 실패-이미 존재하는 username={}", dto.getUsername());
            throw new UserNameAlreadyExistsException(dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("유저 생성 실패-이미 가입된 email={}", dto.getEmail());
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        // 1. 프로필 이미지 먼저 저장 (nullable)
        BinaryContent profileImage = null;
        UUID fileId = UUID.randomUUID();
        if (dto.getFile() != null) {
            try {
                byte[] bytes = dto.getFile().getBytes();
                binaryContentStorage.put(fileId, bytes);
            } catch (IOException e) {
                log.error("유저 생성 실패-프로필 이미지 저장 오류: fileName ={}, username = {}", dto.getFile().getOriginalFilename(), dto.getUsername(), e);
                throw new RuntimeException("유저 생성 중 프로필 이미지 저장 오류 발생", e);
            }

            BinaryContent profileImageEntity = new BinaryContent();
            profileImageEntity.setId(fileId);
            profileImageEntity.setFileName(dto.getFile().getOriginalFilename());
            profileImageEntity.setContentType(dto.getFile().getContentType());
            profileImageEntity.setSize(dto.getFile().getSize());
            profileImage= binaryContentRepository.save(profileImageEntity);
            log.info("유저 생성 중 프로필 이미지 저장 완료: fileId = {}", fileId);
            }

        // 2. 유저 엔티티 생성
        UUID savedFileId = profileImage != null ? profileImage.getId() : null;
        User user = userMapper.userCreateDtoToUser(dto, savedFileId);

        // 3. 유저 저장
        User savedUser = userRepository.save(user);
        log.info("유저 생성 완료: userId = {}, username = {}", savedUser.getId(), savedUser.getUsername());
        return userMapper.userToUserDto(savedUser);
    }
}