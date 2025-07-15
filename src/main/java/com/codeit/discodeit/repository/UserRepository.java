package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void createUser(User user);

  void updateUser(User user);

  void deleteUser(User user);

  void restoreUser(String userName);

  List<User> loadUsers();

  void saveUsers(List<User> users);

  Optional<User> findUserById(UUID id);

  Optional<User> findUserByEmail(String email);

  Optional<User> findUserByUserName(String username);

  Optional<User> findUserByUserId(UUID username);

  Optional<User> findActiveUserByUserId(UUID userId);
}