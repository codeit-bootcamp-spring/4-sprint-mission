package com.codeit.discodeit.repository.jpa_repository;

import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.repository.UserStatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class JpaUserStatusRepository implements UserStatusRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createUserStatus(UserStatus userStatus){
    em.persist(userStatus);
  }

  @Override
  public void updateUserStatus(UserStatus userStatus){
    em.merge(userStatus);
  }

  @Override
  public Optional<UserStatus> findUserStatusByUserStatusId(UUID userStatusId){
    return Optional.ofNullable(em.find(UserStatus.class, userStatusId));
  }

  @Override
  public Optional<UserStatus> findUserStatusByUserId(UUID userId) {
    return em.createQuery(
            "SELECT us FROM UserStatus us WHERE us.user.id = :userId",
            UserStatus.class)
        .setParameter("userId", userId)
        .getResultStream()
        .findFirst();
  }
}
