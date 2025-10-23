package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.RefreshToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByUserId(UUID userId);

  Optional<RefreshToken> findByToken(String token);

  void deleteByUserId(UUID userId);

  void deleteByToken(String token);
}
