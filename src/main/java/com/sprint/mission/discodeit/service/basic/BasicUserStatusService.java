package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    @Override
    public UserStatusResponseDto createUserStatus(UUID userId){

        //create
        //[ ] DTO를 활용해 파라미터를 그룹화합니다.
        //[ ] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
        //[ ] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
        User user = findUserByUserId(userId);

        Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(user.getId());

        if (userStatus.isPresent()) {
            throw new IllegalStateException("이미 존재하는 useStatus 입니다.");
        }

        UserStatus newUserStatus = new UserStatus(user); // 회원 가입할 때는 false로 생성
        userStatusRepository.createUserStatus(newUserStatus);

        return new UserStatusResponseDto(newUserStatus.getId(), user.getId(), user.getUserName(), newUserStatus.getUpdatedAt());
    }

    @Override
    public UserStatusResponseDto updateUserStatus(UUID userId){
        Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(userId);
        User user = findUserByUserId(userId);

        if (userStatus.isEmpty()) {
            createUserStatus(userId);
            throw new IllegalStateException("userStatus가 없어 새로 생성합니다.");
        }

        UserStatus newUserStatus = userStatus.get();

        newUserStatus.updateUpdatedAt();
        userStatusRepository.updateUserStatus(newUserStatus);

        return new UserStatusResponseDto(
                newUserStatus.getId(),
                newUserStatus.getUserId(),
                user.getUserName(),
                newUserStatus.getUpdatedAt()
        );
    }

    @Override
    public List<UserStatusResponseDto> findAllUserStatus(){

        List<UserStatusResponseDto> userStatusResponse = new ArrayList<>();
        List<UserStatus> userStatuses = userStatusRepository.loadUserStatuses();

        for (UserStatus userStatus : userStatuses) {
            User user = findUserByUserId(userStatus.getUserId());
            UserStatusResponseDto userStatusResponseDto = new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), user.getUserName(), userStatus.getUpdatedAt());
            userStatusResponse.add(userStatusResponseDto);
        }

        return userStatusResponse;
    }

    @Override
    public UserStatusResponseDto findUserStatusByUserId(UUID userId){
        Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserStatusId(userId);
        User user = findUserByUserId(userId);

        if (userStatus.isEmpty()) {
            createUserStatus(userId);
            throw new IllegalStateException("userStatus가 없어 새로 생성합니다.");
        }

        return new UserStatusResponseDto(userStatus.get().getId(), user.getId(), user.getUserName(), userStatus.get().getUpdatedAt());
    }


    @Override
    public void deleteUserStatusByUserId(UUID userId){
        userStatusRepository.deleteUserStatusByUserId(userId);
    }

    @Override
    public void deleteUserStatusByUserStatusId(UUID userStatusId){
        userStatusRepository.deleteUserStatusByUserStatusId(userStatusId);
    }


    private User findUserByUserId(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalStateException("해당하는 유저를 찾을 수 없습니다."));
    }
}
