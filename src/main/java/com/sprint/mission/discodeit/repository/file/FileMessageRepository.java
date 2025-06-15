package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.file.FileService;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final Path directory = Paths.get(System.getProperty("user.dir"), "message");
    private static final Path filePath = directory.resolve("message.ser");
    private static final List<Message> messages = new ArrayList<>();

    public FileMessageRepository() {
        FileService.init(directory);
    }

    // Create
    @Override
    public void addMessageAndSave(Message message, User user, Channel channel) {
        messages.add(message);
        user.addMessage(message);
        channel.addMessage(message);

        save();
        synchroWithUserAndChannel();
    }

    public static void save() {
        FileService.save(filePath, messages);
    }

    // Read
    @Override
    public List<Message> getAll() {
        List<Message> fileMessages = FileService.load(directory, Message.class);
        return fileMessages;
    }

    @Override
    public Message getById(UUID id) {
        return getAll().stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found from file."));
    }

    // Update
    @Override
    public void updateContents(Message message, User user, String newContents) {
        if (!messages.contains(message)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        message.setMessageContents(newContents);
        message.setUpdatedAt(System.currentTimeMillis());

        save();
        synchroWithUserAndChannel();
    }

    // Delete
    @Override
    public void delete(Message message, User user, Channel channel) {
        if (!messages.contains(message)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.removeMessageByMessageId(message.getId());
        channel.removeMessageByMessageIdFromActiveChannel(message.getId());

        message.setUpdatedAt(System.currentTimeMillis());
        messages.remove(message);

        save();
        synchroWithUserAndChannel();
    }

    @Override
    public void deleteAll() {
        messages.forEach(message -> {
                    message.setMessageContents("삭제된 메시지 입니다.");
                    message.setUpdatedAt(System.currentTimeMillis());
                    message.getUser().getMessages().clear();
                    message.getChannel().getMessages().clear();
                }
        );
        messages.clear();
        synchroWithUserAndChannel();

        File file = new File(filePath.toUri());
        if (file.delete()) {
            System.out.println("<deleteAll() 성공: 전체 메시지를 삭제하였습니다.>");
        } else {
            System.out.println("<deleteAll() 실패: 전체 메시지 삭제에 실패하였습니다.>");
        }
    }

    private void synchroWithUserAndChannel() {
        FileUserRepository.save();
        FileChannelRepository.save();
    }
}
