package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    public Message save(Message channel);
    public Optional<Message> findId(UUID id);
    public List<Message> findAll(UUID id);
    public boolean existId(UUID id);
    public void deleteId(UUID id);

}
