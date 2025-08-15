package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsernameOrEmail(String username, String email);

  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile " // may not have
      + "JOIN FETCH u.status")
    // must have user status
  List<User> findAllWithProfileAndStatus();
}
