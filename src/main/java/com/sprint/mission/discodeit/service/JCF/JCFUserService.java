package com.sprint.mission.discodeit.service.JCF;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private static JCFUserService instance;
    private final List<User> data;

    private JCFUserService() {
        data = new ArrayList<>();
    }

    public static JCFUserService getInstance() {
        if (instance == null) {
            instance = new JCFUserService();
        }
        return instance;
    }
    //새 유저 등록
    @Override
    public User registUser(String userName) {
        if(userName == null || userName.isEmpty() || userName.equals("*")) {
            //*은 모든 유저 검색에 사용되기 때문에 이름에 사용 불가
            System.out.println("잘못된 입력입니다.");
            return null;
        }
        User newUser = new User(userName);
        data.add(newUser);
        return newUser;
    }

    //유저 삭제
    @Override
    public boolean deleteUser(UUID userId) {
        User userToDelete = findUserById(userId);

        for (Channel c : userToDelete.getChannels()) {
            c.removeUser(userToDelete);
            c.newUpdatedAt();
        }
        for (Message m : userToDelete.getMessages()) {
            m.removeUser();
            m.newUpdatedAt();
            //유저를 삭제할 경우 메세지의 작성자는 deletedUser로 바뀐다.
        }
        userToDelete.setStatus(User.Status.DELETED);
        return true;
    }

    //선택한 유저 정보 출력
    @Override
    public void showUserInfo(User user) {
        if(user == null || !data.contains(user)) {
            System.out.println("해당 유저가 없습니다");
            return;
        }

        if(user.getStatus() == User.Status.DELETED) {
            System.out.println("탈퇴한 유저입니다");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = Instant.ofEpochMilli(user.getCreatedAt())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String createdDate = dateTime.format(formatter);
        System.out.println("유저 이름: " + user.getUserName() + ", 유저 ID: " + user.getUserId() + ", 가입 날짜: " + createdDate+ ", 회원 상태: " + user.getStatus());
    }

    //UID로 유저 검색 후 반환
    @Override
    public User findUserById(UUID userId) {
        return data.stream()
                .filter(user -> user.getUserId().equals(userId)&&user.getStatus() != User.Status.DELETED)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    //검색한 이름의 모든 유저 리스트 반환
    @Override
    public List<User> findUserByName(String userName) {
        if(userName.equals("*")) {
            return data;
            //*입력시 탈퇴한 유저 포함 모든 유저 반환
        }
        List<User> usersByName = data.stream()
                .filter(user -> user.getUserName().equals(userName)&&user.getStatus() != User.Status.DELETED)
                .collect(Collectors.toList());

        if(usersByName.isEmpty()) {
            System.out.println("존재하지 않는 유저입니다.");
        }
        return usersByName;
    }

    //유저 이름 변경
    @Override
    public void changeUserName(UUID userId, String updateUserName) {
        User user = findUserById(userId);
        if(user.getStatus() == User.Status.INACTIVE) {
            System.out.println("휴면중인 유저입니다");
        }

        if(updateUserName != null && !updateUserName.isEmpty()) {
            user.updateUserName(updateUserName);
            user.newUpdatedAt();
        }
    }

    //유저 채널 리스트에 채널 추가
    @Override
    public boolean addNewChannel(UUID userId, Channel channel) {
        if(channel == null) {
            System.out.println("해당 채널이 없습니다");
            return false;
        }

        User user = findUserById(userId);
        if(user.getStatus() == User.Status.INACTIVE) {
            System.out.println("휴면중인 유저입니다");
            return false;
        }

        //유저의 채널리스트에 채널 추가.
        user.addChannel(channel);
        user.newUpdatedAt();
        return true;
    }

    //유저가 참여하고있는 채널 리스트 반환
    @Override
    public List<Channel> getChannelsList(UUID userId) {
        User user = findUserById(userId);

        List<Channel> foundChannels = user.getChannels();
        if(foundChannels.isEmpty()) {
            System.out.println("해당 유저가 참여하고 있는 채널이 없습니다.");
        }
        return foundChannels;
    }

    //해당 채널에서 퇴장
    @Override
    public boolean exitFromChannel(UUID userId, Channel channel) {
        if(channel == null) {
            System.out.println("해당 채널이 없습니다");
            return false;
        }

        User user = findUserById(userId);

        user.removeChannel(channel);
        user.newUpdatedAt();
        channel.newUpdatedAt();
        return true;
    }

    //해당 유저의 상태 변경
    @Override
    public void setUserStatus(UUID userId,User.Status status) {
        User user = findUserById(userId);

        if(status == User.Status.DELETED) {
            deleteUser(user.getUserId());
        }
        else
            user.setStatus(status);
    }

    //유저가 작성한 모든 채팅 리스트 반환
    @Override
    public List<Message> getMessageList(UUID userId) {
        User user = findUserById(userId);

        List<Message> foundMessages = user.getMessages();
        if(foundMessages.isEmpty()) {
            System.out.println("사용자가 작성한 메세지가 없습니다.");
        }
        return foundMessages;
    }
}
