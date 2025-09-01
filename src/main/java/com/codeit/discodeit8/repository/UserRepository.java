package com.codeit.discodeit8.repository;

import com.codeit.discodeit8.entity.User;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findUserByEmail(String email);

  Optional<User> findUserByUsername(String username);
}