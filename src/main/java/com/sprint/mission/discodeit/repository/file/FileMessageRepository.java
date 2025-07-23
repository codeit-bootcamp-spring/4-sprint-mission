package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ActiveStatus;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileMessageRepository implements MessageRepository {
    @Value("${discodeit.repository.file-directory}/Messages.ser")
    private String FILE_PATH;

    @Override
    public List<Message> findAll() {
        List<Message> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            list = (List<Message>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void saveAll(List<Message> messages) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message findById(UUID id) {
        List<Message> list = findAll();
        Message message = list.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
        return message;
    }

    @Override
    public void save(Message message) {
        List<Message> list = findAll();
        if (list.stream().anyMatch(message::equals)) {
            List<Message> updatedList = list.stream().map(c -> c.equals(message) ? message : c)
                    .collect(Collectors.toList());
            saveAll(updatedList);
        } else {
            list.add(message);
            saveAll(list);
        }
    }

    @Override
    public void delete(Message message) {
        List<Message> list = findAll();
        list.remove(message);
        saveAll(list);
    }

    @Override
    public List<Message> findAllActive() {
        List<Message> messages = findAll();
        List<Message> activeMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getActive().equals(ActiveStatus.ACTIVE)) {
                activeMessages.add(message);
            }
        }
        return activeMessages;
    }
}
