package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private static FileMessageService fileMessageService;

    public static FileMessageService getInstance(){
        if(fileMessageService == null){
            fileMessageService = new FileMessageService();
        }
        return fileMessageService;
    }

    @Override
    public Message createMessage(Message message) {
        return saveMessage(message);
    }

    @Override
    public void findAllMessages() {
        List<Message> messages = loadAllMessages();
        messages.forEach(message -> System.out.println(message.toString()));

    }

    @Override
    public Message findMessageById(UUID id) {
        List<Message> messages = loadAllMessages();
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 메세지가 없습니다."));
    }

    @Override
    public void updateMessage(UUID id, String updateMessage) {
        List<Message> messages = loadAllMessages();

        messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .ifPresent(message -> {
                    deleteMessage(message);
                    message.setText(updateMessage);
                    message.setUpdatedAt(System.currentTimeMillis());
                    saveMessage(message);
                });
    }

    @Override
    public void deleteMessage(Message message) {
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\messages\\" + message.getId() + ".ser");
        if (dir.exists()) {
            boolean deleted = dir.delete();
            if (!deleted) {
                System.err.println("메세지 삭제 실패");
            }
        }
    }
    public Message saveMessage(Message message) {
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\messages\\" + message.getId() + ".ser");
        if (dir.exists()) {
            boolean deleted = dir.delete();
            if (!deleted) {
                System.err.println("메세지 삭제 실패");
            }
        }
        try (
                FileOutputStream fos = new FileOutputStream(dir);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public List<Message> loadAllMessages() {
        List<Message> messages = new ArrayList<>();
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\messages");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        for (File file : files) {
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                messages.add((Message) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
}
