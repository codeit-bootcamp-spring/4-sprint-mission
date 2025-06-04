package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.*;

public class JCFMessageService implements MessageService {
    /** 싱글톤 패턴을 적용하여 JCFUserService의 단일 인스턴스만 생성되도록 합니다.*/
    private static final JCFMessageService instance = new JCFMessageService();
    private JCFMessageService() {}
    public static JCFMessageService getInstance() {
        return instance;
    }

    private final Map<String, Message> messages = new HashMap<>();

    @Override
    public void addMessage(Message message) {
        User sender = message.getSender();
        if (sender == null || sender.getUserStatus() != UserStatus.ACTIVE) return;
        messages.put(message.getMessageId().toString(), message);
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages.values());
    }

    /**
     * 해당 메세지 전체를 다른 내용의 메세지로 수정하여 반영합니다.
     * @param replacedContent 수정된 메세지의 내용
     */
    @Override
    public void updateMessage(UUID messageId, String replacedContent) {
        Optional.ofNullable(messages.get(messageId.toString()))
                .ifPresent(message -> {
                    message.setMessageContent(replacedContent);
                    message.setUpdatedAt(System.currentTimeMillis());
                });
    }

    /**
     * 해당 메세지를 부분적으로 수정하여 반영합니다.
     * @param originalContent 해당 메세지의 바꿀 내용
     * @param replacedContent 해당 메세지의 바뀔 내용
     */
    @Override
    public void replaceSubstringInContent(UUID messageId, String originalContent, String replacedContent) {
        Optional.ofNullable(messages.get(messageId.toString()))
                .ifPresent(message -> {
                    String originalMsg = message.getMessageContent();
                    if (!originalMsg.contains(originalContent)) return;
                    String replacedMsg = originalMsg.replaceFirst(originalContent, replacedContent);
                    message.setMessageContent(replacedMsg);
                    message.setUpdatedAt(System.currentTimeMillis());
                });
    }

        @Override
        public void deleteMessage(UUID messageId) {
            messages.remove(messageId.toString());
        }

        @Override
        public List<Message> findMessagesByChannelName(String channelName) {
            List<Message> result = new ArrayList<>();
            for (Message message : messages.values()) {
                if (message.getChannel() != null &&
                    (message.getChannel().getChannelName().equals(channelName))) {
                    result.add(message);
                }
            }
            return result;
        }

        @Override
        public List<Message> findMessagesBySender(User sender) {
            List<Message> result = new ArrayList<>();
            for(Message message : messages.values()) {
                if(message.getSender().equals(sender)) {
                    result.add(message);
                }
            }
            return result;
        }

        @Override
        public List<Message> findMessagesByKeyword(String keyword) {
            List<Message> result = new ArrayList<>();
            for(Message message : messages.values()) {
                if(message.getMessageContent().toLowerCase().contains(keyword.toLowerCase())) {
                    result.add(message);
                }
            }
            return result;
        }

    }

