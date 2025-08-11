package com.codeit.discodeit.repository.jpa_repository;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.repository.ReadStatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class JpaReadStatusRepository implements ReadStatusRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createReadStatus(ReadStatus readStatus){
    em.persist(readStatus);
  }

  @Override
  public void updateReadStatus(ReadStatus readStatus){
    em.merge(readStatus);
  }

  @Override
  public Optional<ReadStatus> findReadStatusesByReadStatusId(UUID readStatusId){
    return Optional.ofNullable(em.find(ReadStatus.class, readStatusId));
  }

  @Override
  public Optional<ReadStatus> findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId){
    return em.createQuery("SELECT rs FROM ReadStatus rs WHERE rs.user.id= :userId AND rs.channel.id = :channelId", ReadStatus.class)
        .setParameter("userId", userId)
        .setParameter("channelId", channelId)
        .getResultStream().findFirst();
  }

  @Override
  public List<ReadStatus> findReadStatusesByUserId(UUID userId){
    return em.createQuery(
            "SELECT rs FROM ReadStatus rs " +
                "JOIN FETCH rs.user " +
                "JOIN FETCH rs.channel " +
                "WHERE rs.user.id = :userId", ReadStatus.class)
        .setParameter("userId", userId)
        .getResultList();

  }

  @Override
  public List<ReadStatus> findReadStatusByChannel(Channel channel){
    return em.createQuery(
            "SELECT rs FROM ReadStatus rs " +
                "JOIN FETCH rs.user " +
                "WHERE rs.channel = :channel", ReadStatus.class)
        .setParameter("channel", channel)
        .getResultList();

  }
}
