package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileMessageRepository  implements MessageRepository {

    private static final FileMessageRepository instance = new FileMessageRepository();
    private static final String SERIALIZED_FILE_PATH = "data/messages.ser";

    public static FileMessageRepository getInstance() {
        return instance;
    }

    private List<Message> loadAll() {
        try (FileInputStream fis = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZED_FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message save(Message message) {
        List<Message> messages = loadAll();
        List<Message> savedMessages = new ArrayList<>(messages);

        savedMessages.add(message);
        saveAll(savedMessages);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return loadAll();
    }

    @Override
    public Message findById(String messageId, User user, Channel channel) {
        return loadAll().stream()
                .filter(message -> message.getMessageId().equals(messageId))
                .filter(message -> user == null || message.getUser().getUserId().equals(user.getUserId()))
                .filter(message -> channel == null || message.getChannel().getChannelId().equals(channel.getChannelId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Message delete(String messageId) {
        List<Message> messages = loadAll();

        Message removed = messages.stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst()
                .orElse(null);

        if (removed != null) {
            messages = messages.stream()
                    .filter(m -> !m.getMessageId().equals(messageId))
                    .toList();
            saveAll(messages);
        }

        return removed;
    }

    @Override
    public List<Message> deleteByChannelId(String channelId) {
        List<Message> messages = loadAll();

        List<Message> toDelete = messages.stream()
                .filter(m -> m.getChannel().getChannelId().equals(channelId))
                .toList();

        List<Message> toKeep = messages.stream()
                .filter(m -> !m.getChannel().getChannelId().equals(channelId))
                .toList();

        saveAll(toKeep);
        return toDelete;
    }

    @Override
    public List<Message> findByUserAndChannel(User user, Channel channel) {
        return loadAll().stream()
                .filter(m -> (user == null || m.getUser().getUserId().equals(user.getUserId())) &&
                        (channel == null || m.getChannel().getChannelId().equals(channel.getChannelId())))
                .toList();
    }

}