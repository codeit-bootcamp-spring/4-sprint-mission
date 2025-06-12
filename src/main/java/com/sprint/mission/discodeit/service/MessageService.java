package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Message sendMessage(String messageBody, Channel channel, User user);
    //메세지 작성

    boolean deleteMessage(UUID messageId);
    //메세지 삭제

    Message fixMessage(UUID messageId,String newBody);
    //메세지 내용 수정

    Optional<Message> findMessageById(UUID messageId);
    //Id로 메세지 반환

    List<Message> findMessageByBody(String messageBody);
    //해당 메세지 내용을 포함하고 있는 모든 메세지 리스트 반환

    void showMessageInfo(Message message);
    //선택한 메세지 정보 출력

    public void showAllMessage();
    //서비스에 저장된 모든 메세지 출력
}
