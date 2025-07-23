package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ActiveStatus;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JCFMessageRepository implements MessageRepository {
    private List<Message> data = new ArrayList<>();

    public List<Message> findAll() {
        return data;
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
        return message;
    }

    @Override
    public void save(Message message) {
        if (data.contains(message)) {
            data.stream()
                    .map(m -> m.equals(message) ? message : m)
                    .forEach(m -> {
                    });
        } else {
            data.add(message);
        }
    }

    @Override
    public void delete(Message message) {
        data.remove(message);
    }

    @Override
    public List<Message> findAllActive() {
        List<Message> activeMessages = new ArrayList<>();
        for (Message message : data) {
            if (message.getActive().equals(ActiveStatus.ACTIVE)) {
                activeMessages.add(message);
            }
        }
        return activeMessages;
    }
}
