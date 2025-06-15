package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // Create
    public User createUser(String name);

    // Read
    public List<User> getUsers();

    public User getUserById(UUID id);

    // Update
    public void updateActiveUserNameByUser(User user, String name);

    public void updateUserStatusByUserExceptQuitUser(User user, UserType.UserStatus userStatus);

    // Delete
    public void deleteUserByUser(User user);

    public void deleteAllUsers();

    // 참여중인 채널 관련 메서드
    // 채널 참가
    public void joinChannelOnlyActiveUser(User user, Channel channel);

    // 채널 나가기
    public void outChannelOnlyActiveUser(User user, Channel channel);

}
