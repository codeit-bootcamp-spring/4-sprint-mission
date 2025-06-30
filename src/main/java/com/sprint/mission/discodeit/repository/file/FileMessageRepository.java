package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Component
public class FileMessageRepository implements MessageRepository {

    @Override
    public Message save(Message channel) {
        return null;
    }

    @Override
    public Optional<Message> findId(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public boolean existId(UUID id) {
        return false;
    }

    @Override
    public void deleteId(UUID id) {

    }
}
