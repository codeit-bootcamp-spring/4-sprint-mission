package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {

    User registUser(String userName);
    //신규 유저 등록

    boolean deleteUser(UUID userId);
    //유저 정보 삭제

    void showUserInfo(User user);
    //선택한 유저 출력

    User findUserById(UUID userId);
    //userId로 유저 반환

    List<User> findUserByName(String userName);
    //이름이 userName인 모든 유저 리스트 반환

    void changeUserName(UUID userId, String updateUserName);
    //유저 정보 수정

    boolean addNewChannel(UUID userId, Channel channel);
    //유저가 참여하고 았는 채널 추가

    List<Channel> getChannelsList(UUID userId);
    //유저가 참여하고있는 채널 리스트 조회

    boolean exitFromChannel(UUID userId, Channel channel);
    //해당 채널에서 퇴장

    void setUserStatus(UUID userId, User.Status status);
    //해당 유저의 상태 변경

    List<Message> getMessageList(UUID userId);
    //유저가 작성한 모든 메세지 기록 조회.
}

