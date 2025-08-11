package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void createUser(User user);

  void updateUser(User user);

  void deleteUser(User user);

  List<User> loadUsers();

  Optional<User> findUserByUserId(UUID id);

  Optional<User> findUserByEmail(String email);

  Optional<User> findUserByUserName(String username);

  List<User> findUserListByUserIdList(List<UUID> userIdList);
}