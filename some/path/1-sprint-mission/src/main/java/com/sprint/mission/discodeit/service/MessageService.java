package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    public Message addMessage(Message message);//메세지 입력
    public List<Message> getMessages();//모든 메세지 출력
    public Optional<Message> findMessageById(UUID messageId);//특정 메세지 출력
    public List<Message> findMessagesByContentContains(String MessageName);//특정 단어가 포함된 메세지 출력
    public void updateMessage(UUID MessageId, String updatedText);//메세지 수정
    public void deleteMessage(Message message);//메세지 삭제
    public void deleteAllMessagesByUser(User user);//유저가 쓴 모든 메세지 삭제
}
