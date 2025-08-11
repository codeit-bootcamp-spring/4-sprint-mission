package com.codeit.discodeit.repository.jpa_repository;

import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.repository.MessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class JpaMessageRepository implements MessageRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createMessage(Message message){
    em.persist(message);
  }

  @Override
  public void deleteMessage(Message message){
    em.remove(message);
  }

  @Override
  public void updateMessage(Message message){
    em.merge(message);
  }

  @Override
  public Optional<Message> findMessageByMessageId(UUID messageId){
    return Optional.ofNullable(em.find(Message.class, messageId));
  }

  @Override
  public List<Message> findMessagesByChannelId(UUID channelId) {
    return em.createQuery(
            "SELECT DISTINCT m FROM Message m JOIN FETCH m.author JOIN FETCH m.channel JOIN FETCH m.attachments WHERE m.channel.id = :channelId ORDER BY m.createdAt ASC",
            Message.class)
        .setParameter("channelId", channelId)
        .getResultList();
  }
  
  @Override
  public Optional<Message> findLastMessageInChannel(UUID channelId) {
    return em.createQuery(
            "SELECT m FROM Message m WHERE m.channel.id = :channelId ORDER BY m.createdAt DESC",
            Message.class)
        .setParameter("channelId", channelId)
        .setMaxResults(1)
        .getResultStream()
        .findFirst();
  }
}