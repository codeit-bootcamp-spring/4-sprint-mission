//package com.sprint.mission.discodeit.service.JCF;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//public class JCFMessageService implements MessageService {
//
//    private static JCFMessageService instance;
//    private final MessageRepository repo; // 데이터 저장소 분리
//
//    private JCFMessageService() {
//        repo = JCFMessageRepository.getInstance();
//    }
//
//    public static JCFMessageService getInstance() {
//        if (instance == null) {
//            instance = new JCFMessageService();
//        }
//        return instance;
//    }
//
//    // 메세지 작성
//    @Override
//    public Message sendMessage(String messageBody, Channel channel, User user){
//        if(channel == null || user == null) {
//            System.out.println("채널 또는 유저가 없습니다");
//            return null;
//        }
//        if(user.getStatus()!= User.Status.ACTIVE) {
//            System.out.println("휴면중 혹은 탈퇴한 유저입니다");
//            return null;
//        }
//        if(messageBody == null || messageBody.isEmpty()){
//            System.out.println("메세지의 본문이 비었습니다");
//            return null;
//        }
//        Message newMessage = new Message(messageBody, user, channel);
//        newMessage.getUser().addMessage(newMessage);
//        newMessage.getChannel().addMessage(newMessage);
//        user.addChannel(channel);
//        repo.save(newMessage); // 저장소에 저장
//        return newMessage;
//    }
//
//    // 메세지 삭제
//    @Override
//    public boolean deleteMessage(UUID messageId) {
//        Optional<Message> deletedMessageOpt = findMessageById(messageId);
//        if (deletedMessageOpt.isEmpty()) return false;
//        Message deletedMessage = deletedMessageOpt.get();
//        deletedMessage.getUser().removeMessage(deletedMessage);
//        deletedMessage.getChannel().removeMessage(deletedMessage);
//        repo.delete(messageId); // 저장소에서 삭제
//        return true;
//    }
//
//    // 메세지 내용 수정
//    @Override
//    public Message fixMessage(UUID messageId, String newBody){
//        Optional<Message> messageOpt = findMessageById(messageId);
//        if (messageOpt.isEmpty()) return null;
//        Message message = messageOpt.get();
//        message.updateMessageBody(newBody);
//        repo.save(message); // 변경사항 저장소에 반영
//        return message;
//    }
//
//    @Override
//    // Id로 메세지 정보 조회 (Optional 적용)
//    public Optional<Message> findMessageById(UUID messageId){
//        Message message = repo.findById(messageId);
//        if (message == null) {
//            System.out.println("해당하는 메세지가 없습니다");
//            return Optional.empty();
//        }
//        return Optional.of(message);
//    }
//
//    // 메세지 내용으로 메세지 조회(검색한 내용을 포함하고 있는 전체 메세지 조회)
//    @Override
//    public List<Message> findMessageByBody(String messageBody){
//        List<Message> messages = repo.findByBody(messageBody);
//        if(messages.isEmpty()){
//            System.out.println("해당 내용을 포함하는 메세지가 없습니다.");
//        }
//        return messages;
//    }
//
//    // 선택한 메세지 정보 출력
//    @Override
//    public void showMessageInfo(Message message) {
//        if(message == null || !repo.isContains(message.getMessageId())) {
//            System.out.println("해당 메세지가 없습니다");
//            return;
//        }
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime dateTime = Instant.ofEpochMilli(message.getCreatedAt())
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//
//        String createdDate = dateTime.format(formatter);
//        System.out.println("메세지 내용: " + message.getMessageBody() +
//                ", 작성자: " + message.getUser().getUserName() +
//                ", 채널: " + message.getChannel().getChannelName() +
//                ", 작성 날짜: " + createdDate);
//    }
//
//    // 서비스에 저장된 모든 메세지 출력
//    @Override
//    public void showAllMessage() {
//        List<Message> allMessages = repo.findAll();
//        if(allMessages.isEmpty()) {
//            System.out.println("메세지가 없습니다");
//            return;
//        }
//
//        for(Message message : allMessages) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            LocalDateTime dateTime = Instant.ofEpochMilli(message.getCreatedAt())
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDateTime();
//
//            String createdDate = dateTime.format(formatter);
//            System.out.println("메세지 내용: " + message.getMessageBody() +
//                    ", 작성자: " + message.getUser().getUserName() +
//                    ", 채널: " + message.getChannel().getChannelName() +
//                    ", 작성 날짜: " + createdDate);
//        }
//    }
//}
