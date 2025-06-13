package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private static final String DATA_FILE = "data/messages.ser";
    private final Map<UUID, Message> messagesCache = new ConcurrentHashMap<>();

    public FileMessageService() {
        loadAllMessagesToCache();
    }

    public void saveMessage() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            List<Message> messages = new ArrayList<>(messagesCache.values());
            oos.writeObject(messages);
        }catch (IOException e){
            throw new RuntimeException("저장 오류: " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAllMessagesToCache() {
        File file = new File(DATA_FILE);
        if(!file.exists()){
            return;
        }
        try(ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(file))){
            List<Message> messages = (List<Message>) ois.readObject();
            messages.forEach(message -> {messagesCache.put(message.getMessageId(), message);});
        }catch (IOException | ClassNotFoundException e){
            System.out.println("불러오기 오류: " + e.getMessage());
        }
    }

    @Override
    public Message addMessage(String content, User user, Channel channel) {
        Message message = new Message(content, user, channel);
        messagesCache.put(message.getMessageId(), message);
        saveMessage();
        return message;
    }

    @Override
    public List<Message> findAllMessage() {
        return messagesCache.values().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        Message message = messagesCache.get(messageId);
        if(message == null || message.getState() == MessageState.DELETED){
            return Optional.empty();
        }
        return Optional.of(message);
    }

    @Override
    public List<Message> findMessagesByContentContains(String MessageContents) {
        return messagesCache.values().stream()
                .filter(message -> message.getContent().contains(MessageContents))
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID messageId,String updatedText) {
        Message message = messagesCache.get(messageId);
        if(message != null && message.getState() != MessageState.DELETED){
            message.setContents(updatedText);
            message.setUpdatedAt();
            saveMessage();
        }
    }

    @Override
    public void deleteMessage(Message message) {
        Message cached = messagesCache.get(message.getMessageId());
        if(cached != null && cached.getState() != MessageState.DELETED){
            cached.deletedMessageState();
            saveMessage();
        }
    }

    @Override
    public void deleteAllMessagesByUser(User user) {
        user.getMessages().stream()
                .filter(m -> m.getState() != MessageState.DELETED)
                .forEach(message -> {
                    message.deletedMessageState();
                    messagesCache.put(message.getMessageId(), message);
                });
        user.removeAllMessages();
        saveMessage();
    }

}
