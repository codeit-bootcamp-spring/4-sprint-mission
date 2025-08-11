package com.codeit.discodeit.repository.jpa_repository;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.repository.ChannelRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class JpaChannelRepository implements ChannelRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createChannel(Channel channel){
    em.persist(channel);
  }

  public void deleteChannel(Channel channel){
    em.remove(channel);
  }

  @Override
  public List<Channel> loadChannels(){
    return em.createQuery("select c from Channel c", Channel.class).getResultList();
  }

  @Override
  public List<Channel> findAllPublicChannel(){
    return em.createQuery("SELECT c FROM Channel c WHERE c.type= :PUBLIC_CHANNEL ", Channel.class)
        .setParameter("PUBLIC_CHANNEL", ChannelType.PUBLIC).getResultList();
  }

  @Override
  public void updateChannel(Channel channel){
    em.merge(channel);
  }

  @Override
  public Optional<Channel> findChannelByChannelName(String channelName){
    return em.createQuery("select c from Channel c where c.name = :channelName", Channel.class)
        .setParameter("channelName", channelName)
        .getResultStream().findFirst();
  }

  @Override
  public Optional<Channel> findChannelByChannelId(UUID channelId){
    return Optional.ofNullable(em.find(Channel.class, channelId));
  }
}
