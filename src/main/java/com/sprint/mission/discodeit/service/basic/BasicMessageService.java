package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository repo;
    public BasicMessageService (MessageRepository messageRepository) {
        this.repo = messageRepository;
    }
    @Override
    public Message sendMessage(String messageBody, Channel channel, User user) {
        if (channel == null || user == null) {
            System.out.println("채널 또는 유저가 없습니다");
            return null;
        }
        if (user.getStatus() != User.Status.ACTIVE) {
            System.out.println("휴면중 혹은 탈퇴한 유저입니다");
            return null;
        }
        if (messageBody == null || messageBody.isEmpty()) {
            System.out.println("메세지의 본문이 비었습니다");
            return null;
        }

        Message newMessage = new Message(messageBody, user, channel);
        user.addMessage(newMessage);
        channel.addMessage(newMessage);
        user.addChannel(channel);
        // 해당 채널에서 메세지를 작성했으므로 유저는 자동으로 채널에 참여

        repo.save(newMessage);
        FileUserRepository.getInstance().saveAllUsers();
        FileChannelRepository.getInstance().saveAllChannels();

        return newMessage;
    }

    // 메세지 삭제
    @Override
    public boolean deleteMessage(UUID messageId) {
        Optional<Message> deletedMessageOpt = findMessageById(messageId);
        if (deletedMessageOpt.isEmpty()) {
            System.out.println("삭제할 메시지가 없습니다.");
            return false;
        }
        Message deletedMessage = deletedMessageOpt.get();

        deletedMessage.getUser().removeMessage(deletedMessage);
        deletedMessage.getChannel().removeMessage(deletedMessage);
        FileUserRepository.getInstance().saveAllUsers();
        FileChannelRepository.getInstance().saveAllChannels();

        repo.delete(messageId);
        return true;
    }

    // 메세지 본문 내용 수정
    @Override
    public Message fixMessage(UUID messageId, String newBody) {
        Optional<Message> messageOpt = findMessageById(messageId);
        if (messageOpt.isEmpty()) {
            System.out.println("수정할 메시지가 없습니다.");
            return null;
        }
        Message message = messageOpt.get();
        message.updateMessageBody(newBody);
        repo.save(message);
        return message;
    }

    // UUID로 메세지 검색 (Optional 적용)
    public Optional<Message> findMessageById(UUID messageId) {
        Message message = repo.findById(messageId);
        if (message == null) {
            System.out.println("해당하는 메세지가 없습니다");
            return Optional.empty();
        }
        return Optional.of(message);
    }

    // 본문 내용으로 메세지 검색
    @Override
    public List<Message> findMessageByBody(String messageBody) {
        List<Message> result = repo.findByBody(messageBody);
        if (result.isEmpty()) {
            System.out.println("해당 내용을 포함하는 메세지가 없습니다.");
        }
        return result;
    }

    // 메세지 정보 출력
    @Override
    public void showMessageInfo(Message message) {
        if (message == null || !repo.isContains(message.getMessageId())) {
            System.out.println("해당 메세지가 없습니다");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = Instant.ofEpochMilli(message.getCreatedAt())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        String createdDate = dateTime.format(formatter);
        System.out.println("메세지 내용: " + message.getMessageBody() +
                ", 작성자: " + message.getUser().getUserName() +
                ", 채널: " + message.getChannel().getChannelName() +
                ", 작성 날짜: " + createdDate);
    }

    // 전체 메세지 출력
    @Override
    public void showAllMessage() {
        List<Message> allMessages = repo.findAll();
        if (allMessages.isEmpty()) {
            System.out.println("메세지가 없습니다");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Message message : allMessages) {
            LocalDateTime dateTime = Instant.ofEpochMilli(message.getCreatedAt())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            String createdDate = dateTime.format(formatter);
            System.out.println("메세지 내용: " + message.getMessageBody() +
                    ", 작성자: " + message.getUser().getUserName() +
                    ", 채널: " + message.getChannel().getChannelName() +
                    ", 작성 날짜: " + createdDate);
        }
    }
}
