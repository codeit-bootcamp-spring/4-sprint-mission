package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static FileMessageRepository fileMessageRepository;

    public static FileMessageRepository getInstance() {
        if (fileMessageRepository == null) {
            fileMessageRepository = new FileMessageRepository();
        }
        return fileMessageRepository;
    }

    @Override
    public Message sendMessage(Path dir, Message message) {
        if (Files.notExists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {}
        }
        Path filePath = dir.resolve(message.getId() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
            ) {
                oos.writeObject(message);
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public List<Message> loadAllMessages(Path dir) {
        if (Files.exists(dir)) {
            try {
                List<Message> messages = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                                ) {
                                    Message data = (Message) (ois.readObject());
                                    return data;
                            } catch(IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return messages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Message> loadMessageById(Path dir, UUID id) {
        if  (Files.exists(dir)) {
            try {
                Optional<Message> message = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                Message data = (Message)(ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(m -> m.getId().equals(id))
                        .findFirst();
                return message;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteMessage(Path dir, Message message) {
        Path filePath = dir.resolve(message.getId() + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(filePath + "파일 삭제 실패", e);
        }
    }
}
