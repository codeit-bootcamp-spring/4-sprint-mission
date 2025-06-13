package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    // Create
    public Optional<User> addUser(String name);

    // default 유저
    public User temp();

    // Read
    public List<User> getUsers();

    public Optional<User> getUserById(UUID id);

    // Update
    public void updateUserById(UUID id, String name);

    public void updateUserStatusById(UUID id, User.Status status);

    // Delete
    public void deleteUserById(UUID id);

    // 참여중인 채널 관련 메서드
    // 채널 참가
    public void joinChannel(UUID id, Channel nullableChannel);

    // 채널 나가기
    public void outChannel(UUID id, Channel nullableChannel);

    // 유저 활성 관련 코드
    // 유저 활성화
    public void activateUserById(UUID id);

    // 유저 비활성화
    public void deactivateUserById(UUID id);
}
