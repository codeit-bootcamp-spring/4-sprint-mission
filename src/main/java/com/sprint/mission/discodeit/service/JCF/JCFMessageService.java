package com.sprint.mission.discodeit.service.JCF;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private static JCFMessageService instance;
    private final List<Message> data;

    private JCFMessageService() {
        data = new ArrayList<>();
    }

    public static JCFMessageService getInstance() {
        if (instance == null) {
            instance = new JCFMessageService();
        }
        return instance;
    }

    //메세지 작성
    @Override
    public Message sendMessage(String messageBody, Channel channel, User user){
        if(channel == null || user == null) {
            System.out.println("채널 또는 유저가 없습니다");
            return null;
        }
        if(user.getStatus()!= User.Status.ACTIVE) {
            System.out.println("휴면중 혹은 탈퇴한 유저입니다");
            return null;
        }
        if(messageBody == null || messageBody.isEmpty()){
            System.out.println("메세지의 본문이 비었습니다");
            return null;
        }
        Message newMessage = new Message(messageBody, user, channel);
        newMessage.getUser().addMessage(newMessage);
        newMessage.getChannel().addMessage(newMessage);
        //채널과 유저에 작성한 메세지 추가
        user.addChannel(channel);
        //해당 채널에서 유저가 메세지를 작성했으므로 유저와 채널 연결
        data.add(newMessage);
        return newMessage;
    }

    //메세지 삭제
    @Override
    public boolean deleteMessage(UUID messageId) {
        Message deletedMessage = findMessageById(messageId);
        deletedMessage.getUser().removeMessage(deletedMessage);
        deletedMessage.getChannel().removeMessage(deletedMessage);
        //채널과 유저에서 해당 메세지 제거
        data.remove(deletedMessage);
        return true;
    }

    //메세지 내용 수정
    @Override
    public Message fixMessage(UUID messageId, String newBody){
        Message message = findMessageById(messageId);

        message.updateMessageBody(newBody);
        return message;
    }

    //Id로 메세지 정보 조회
    @Override
    public Message findMessageById(UUID messageId){
        return data.stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 메세지가 없습니다"));
    }

    //메세지 내용으로 메세지 조회(검색한 내용을 포함하고 있는 전체 메세지 조회)
    @Override
    public List<Message> findMessageByBody(String messageBody){
        List<Message> messages = data.stream()
                .filter(message -> message.getMessageBody().contains(messageBody))
                .collect(Collectors.toList());
        if(messages.isEmpty()){
            System.out.println("해당 내용을 포함하는 메세지가 없습니다.");
        }
        return messages;
    }

    //선택한 메세지 정보 출력
    @Override
    public void showMessageInfo(Message message) {
        if(message == null || !data.contains(message)) {
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

    //서비스에 저장된 모든 메세지 출력
    @Override
    public void showAllMessage() {
        if(data.isEmpty()) {
            System.out.println("메세지가 없습니다");
            return;
        }

        for(Message message : data) {

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
    }
}
