package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "data/messages.ser";

    @Override
    public Optional<Message> findById(UUID messageId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<Message> messages = (List<Message>) ois.readObject();
            return messages.stream()
                    .filter(message -> message.getMessageId().equals(messageId))
                    .findFirst();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void save(Message message) {
        List<Message> messages;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            messages = (List<Message>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            messages = new ArrayList<>();
        }

        messages.removeIf(m -> m.getMessageId().equals(message.getMessageId()));
        messages.add(message);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(UUID messageId) {
        List<Message> messages;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            messages = (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            messages = new ArrayList<>();
        }

        messages.removeIf(m -> m.getMessageId().equals(messageId));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> findAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
