package com.codeit.discodeit.repository.jpa_repository;

import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaUserRepository implements UserRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createUser(User user){
    em.persist(user);
  }

  @Override
  public void updateUser(User user){
    em.merge(user);
  }

  @Override
  public void deleteUser(User user){
    em.remove(user);
  }

  @Override
  public List<User> loadUsers() {
    return em.createQuery("SELECT u FROM User u", User.class)
        .getResultList();
  }
  @Override
  public Optional<User> findUserByUserId(UUID userId) {
    return Optional.ofNullable(em.find(User.class, userId));
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst();
  }

  @Override
  public Optional<User> findUserByUserName(String username) {
    return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
        .setParameter("username", username)
        .getResultStream()
        .findFirst();
  }

  @Override
  public List<User> findUserListByUserIdList(List<UUID> userIdList){
    return em.createQuery("SELECT u FROM User u WHERE u.id in :userIdList", User.class)
        .setParameter("userIdList", userIdList)
        .getResultList();
  }

}
