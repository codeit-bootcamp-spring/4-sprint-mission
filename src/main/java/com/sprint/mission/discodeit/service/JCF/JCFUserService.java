//package com.sprint.mission.discodeit.service.JCF;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//public class JCFUserService implements UserService {
//
//    private static JCFUserService instance;
//    private final UserRepository repo;// 데이터 저장소 분리
//
//    private JCFUserService() {
//        repo = JCFUserRepository.getInstance();
//    }
//
//    public static JCFUserService getInstance() {
//        if (instance == null) {
//            instance = new JCFUserService();
//        }
//        return instance;
//    }
//
//    // 새 유저 등록
//    @Override
//    public User registUser(String userName) {
//        // *은 모든 유저 검색에 사용되기 때문에 이름에 사용 불가
//        if(userName == null || userName.isEmpty() || userName.equals("*")) {
//            System.out.println("잘못된 입력입니다.");
//            return null;
//        }
//        User newUser = new User(userName);
//        return repo.save(newUser);
//    }
//
//    // 유저 삭제
//    @Override
//    public boolean deleteUser(UUID userId) {
//        Optional<User> userToDeleteOpt = findUserById(userId);
//        if (userToDeleteOpt.isEmpty()) return false;
//        User userToDelete = userToDeleteOpt.get();
//
//        // 유저가 속한 모든 채널에서 해당 유저 제거
//        for (Channel c : userToDelete.getChannels()) {
//            c.removeUser(userToDelete);
//            c.newUpdatedAt();
//        }
//        // 유저가 작성한 모든 메시지에서 유저 정보 제거
//        for (Message m : userToDelete.getMessages()) {
//            m.removeUser();
//            m.newUpdatedAt();
//            // 유저를 삭제할 경우 메시지의 작성자는 deletedUser로 바뀐다.
//        }
//        userToDelete.setStatus(User.Status.DELETED);
//        return true;
//    }
//
//    // 선택한 유저 정보 출력
//    @Override
//    public void showUserInfo(User user) {
//        if(user == null || !repo.isContains(user.getUserId())) {
//            System.out.println("해당 유저가 없습니다");
//            return;
//        }
//
//        if(user.getStatus() == User.Status.DELETED) {
//            System.out.println("탈퇴한 유저입니다");
//            return;
//        }
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime dateTime = Instant.ofEpochMilli(user.getCreatedAt())
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//        String createdDate = dateTime.format(formatter);
//        System.out.println("유저 이름: " + user.getUserName() + ", 유저 ID: " + user.getUserId() + ", 가입 날짜: " + createdDate+ ", 회원 상태: " + user.getStatus());
//    }
//
//    @Override
//    // UID로 유저 검색 후 반환 (Optional 적용)
//    public Optional<User> findUserById(UUID userId) {
//        User user = repo.findById(userId);
//        if(user == null || user.getStatus() == User.Status.DELETED) {
//            System.out.println("해당 유저가 없습니다.");
//            return Optional.empty();
//        }
//        return Optional.of(user);
//    }
//
//    // 검색한 이름의 모든 유저 리스트 반환 (빈 리스트 반환)
//    @Override
//    public List<User> findUserByName(String userName) {
//        if(userName.equals("*")) {
//            return repo.findAll(); // *입력시 탈퇴한 유저 포함 모든 유저 반환
//        }
//        List<User> usersByName = repo.findByName(userName);
//
//        if(usersByName.isEmpty()) {
//            System.out.println("존재하지 않는 유저입니다.");
//        }
//        return usersByName;
//    }
//
//    // 유저 이름 변경
//    @Override
//    public void changeUserName(UUID userId, String updateUserName) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return;
//        User user = userOpt.get();
//
//        if(user.getStatus() == User.Status.INACTIVE) {
//            System.out.println("휴면중인 유저입니다");
//            return;
//        }
//
//        if(updateUserName != null && !updateUserName.isEmpty()) {
//            user.updateUserName(updateUserName);
//            user.newUpdatedAt();
//        }
//    }
//
//    // 유저 채널 리스트에 채널 추가
//    @Override
//    public boolean addNewChannel(UUID userId, Channel channel) {
//        if(channel == null) {
//            System.out.println("해당 채널이 없습니다");
//            return false;
//        }
//
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return false;
//        User user = userOpt.get();
//
//        if(user.getStatus() == User.Status.INACTIVE) {
//            System.out.println("휴면중인 유저입니다");
//            return false;
//        }
//
//        // 유저의 채널리스트에 채널 추가
//        user.addChannel(channel);
//        user.newUpdatedAt();
//        return true;
//    }
//
//    // 유저가 참여하고있는 채널 리스트 반환 (빈 리스트 반환)
//    @Override
//    public List<Channel> getChannelsList(UUID userId) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) {
//            System.out.println("존재하지 않는 유저입니다.");
//            return Collections.emptyList();
//        }
//        List<Channel> foundChannels = userOpt.get().getChannels();
//        if(foundChannels.isEmpty()) {
//            System.out.println("해당 유저가 참여하고 있는 채널이 없습니다.");
//        }
//        return foundChannels;
//    }
//
//    // 해당 채널에서 퇴장
//    @Override
//    public boolean exitFromChannel(UUID userId, Channel channel) {
//        if(channel == null) {
//            System.out.println("해당 채널이 없습니다");
//            return false;
//        }
//
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return false;
//        User user = userOpt.get();
//
//        user.removeChannel(channel);
//        user.newUpdatedAt();
//        channel.newUpdatedAt();
//        return true;
//    }
//
//    // 해당 유저의 상태 변경
//    @Override
//    public void setUserStatus(UUID userId,User.Status status) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return;
//        User user = userOpt.get();
//
//        if(status == User.Status.DELETED) {
//            System.out.println("상태 변경에서는 회원 탈퇴를 할 수 없습니다.");
//            // 회원 탈퇴는 deleteUser에서만 가능
//            return;
//        }
//        user.setStatus(status);
//    }
//
//    // 유저가 작성한 모든 채팅 리스트 반환 (빈 리스트 반환)
//    @Override
//    public List<Message> getMessageList(UUID userId) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) {
//            System.out.println("존재하지 않는 유저입니다.");
//            return Collections.emptyList();
//        }
//        List<Message> foundMessages = userOpt.get().getMessages();
//        if(foundMessages.isEmpty()) {
//            System.out.println("사용자가 작성한 메세지가 없습니다.");
//        }
//        return foundMessages;
//    }
//}
