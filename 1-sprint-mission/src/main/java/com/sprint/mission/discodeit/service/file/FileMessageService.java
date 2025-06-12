package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService, Serializable {

    private final List<Message> data;
    private static final FileMessageService instance = new FileMessageService();
    private static final String FILE_PATH = "study/messages.txt";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SERIALIZED_FILE_PATH = "study/messages.ser";

    private FileMessageService() {
        this.data = new ArrayList<>();
    }

    public static FileMessageService getInstance() {
        return instance;
    }

    //영속화를 위한 모든 채널을 찾는 메소드
    public List<Message> findAllMessages(User user, Channel channel) {
        List<Message> messages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();  //헤더 스킵

            while ((line = reader.readLine()) != null) {

                Message message = Message.fromCSV(line, user, channel);
                if (message != null) {
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }

    //영속화를 위한 모든 채널을 저장하는 메소드
    public void saveAllMessages(List<Message> messages) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("username, channelname, content" + LINE_SEPARATOR);

            for (Message message : messages) {
                writer.write(message.toCSV() + LINE_SEPARATOR);
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //직렬화 저장 메소드
    public void saveSerializedMessages(List<Message> messages) {
        try (FileOutputStream fos = new FileOutputStream(SERIALIZED_FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //역직렬화 로드 메소드
    public List<Message> loadSerializedMessages() {
        List<Message> messages = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            messages = (List<Message>) ois.readObject();

            data.clear();
            data.addAll(messages);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        data.add(message);
        channel.addMessage(message);
        message.setUser(user);
        message.setChannel(channel);

        saveAllMessages(data);

        saveSerializedMessages(data);

        return message;
    }

    @Override
    public List<Message> getMessages(User user, Channel channel) {

        List<Message> serializedMessages = loadSerializedMessages();
        List<Message> csvMessages = findAllMessages(user, channel);

        Map<String, Message> messageMap = new HashMap<>();
        for (Message message : serializedMessages) {
            messageMap.put(message.getMessageId(), message);
        }
        for (Message message : csvMessages) {
            messageMap.put(message.getMessageId(), message);
        }

        List<Message> messages = new ArrayList<>(messageMap.values());

        data.clear();
        data.addAll(messageMap.values());

        return messages;
    }

    @Override
    public Message getMessageById(String messageId, User user, Channel channel) {
        Map<String, Message> mergedMap = new HashMap<>();

        List<Message> serializedMessages = loadSerializedMessages();
        if (serializedMessages != null) {
            for (Message msg : serializedMessages) {
                mergedMap.put(msg.getMessageId(), msg);
            }
        }

        List<Message> csvMessages = findAllMessages(user, channel);
        for (Message message : csvMessages) {
            mergedMap.put(message.getMessageId(), message); // 같은 ID 있으면 CSV 메시지로 덮어쓰기
        }

        // 2. data 리스트 최신화
        data.clear();
        data.addAll(mergedMap.values());

        // 3. messageId로 조회
        return mergedMap.get(messageId);
    }

    @Override
    public Message updateMessage(String messageId, String newContent) {
        for (Message message : data) {
            if (message.getMessageId().equals(messageId)) {
                message.setContent(newContent);
                message.setUpdatedAt(System.currentTimeMillis());

                List<Message> messageList = new ArrayList<>(data);

                saveAllMessages(messageList);
                saveSerializedMessages(messageList);

                return message;
            }
        }
        return null;
    }

    @Override
    public Message deleteMessage(String messageId) {
        Iterator<Message> iterator = data.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (message.getMessageId().equals(messageId)) {
                iterator.remove();

                List<Message> messageList = new ArrayList<>(data);
                saveAllMessages(messageList);
                saveSerializedMessages(messageList);

                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> deleteMessagesByChannelId(String channelId) {
        List<Message> deletedmessages = new ArrayList<>();
        for (Message message : data) {
            if (message.getChannel().getChannelId().equals(channelId)) {
                deletedmessages.add(message);
            }
        }

        data.removeIf(message -> message.getChannel().getChannelId().equals(channelId));

        List<Message> messageList = new ArrayList<>(data);
        saveAllMessages(messageList);
        saveSerializedMessages(messageList);

        return deletedmessages;
    }

}