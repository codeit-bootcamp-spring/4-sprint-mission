//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class FileUserService implements UserService {
//    private static FileUserService instance;
//    private final UserRepository repo;
//
//    private FileUserService() {
//        repo = FileUserRepository.getInstance();
//    }
//
//    public static FileUserService getInstance() {
//        if (instance == null) {
//            instance = new FileUserService();
//        }
//        return instance;
//    }
//
//    // 새 유저 등록
//    @Override
//    public User registUser(String userName) {
//        // *은 모든 유저 검색에 사용되기 때문에 이름에 사용 불가
//        if (userName == null || userName.isEmpty() || userName.equals("*")) {
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
//            FileChannelRepository.getInstance().saveAllChannels();
//            c.newUpdatedAt();
//        }
//        // 유저가 작성한 모든 메시지에서 유저 정보 제거
//        for (Message m : userToDelete.getMessages()) {
//            m.removeUser();
//            FileMessageRepository.getInstance().saveAllMessages();
//            m.newUpdatedAt();
//        }
//        userToDelete.setStatus(User.Status.DELETED);
//        return true;
//    }
//
//    // 선택한 유저 정보 출력
//    @Override
//    public void showUserInfo(User user) {
//        if (user == null || !repo.isContains(user.getUserId())) {
//            System.out.println("해당 유저가 없습니다");
//            return;
//        }
//        if (user.getStatus() == User.Status.DELETED) {
//            System.out.println("탈퇴한 유저입니다");
//            return;
//        }
//        System.out.println("유저 이름: " + user.getUserName() + ", 유저 ID: " + user.getUserId() + ", 회원 상태: " + user.getStatus());
//    }
//
//    // UID로 유저 검색 후 반환 (Optional 적용)
//    public Optional<User> findUserById(UUID userId) {
//        User user = repo.findById(userId);
//        if (user != null && user.getStatus() != User.Status.DELETED) {
//            return Optional.of(user);
//        }
//        System.out.println("존재하지 않는 유저입니다");
//        return Optional.empty();
//    }
//
//    // 검색한 이름의 모든 유저 리스트 반환
//    @Override
//    public List<User> findUserByName(String userName) {
//        if (userName.equals("*")) {
//            return repo.findAll(); // *입력시 탈퇴한 유저 포함 모든 유저 반환
//        }
//        List<User> usersByName = repo.findByName(userName);
//        if (usersByName.isEmpty()) {
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
//        if (user.getStatus() == User.Status.INACTIVE) {
//            System.out.println("휴면중인 유저입니다");
//            return;
//        }
//        if (updateUserName != null && !updateUserName.isEmpty()) {
//            user.updateUserName(updateUserName);
//            user.newUpdatedAt();
//            repo.save(user);
//        }
//    }
//
//    // 유저 채널 리스트에 채널 추가
//    @Override
//    public boolean addNewChannel(UUID userId, Channel channel) {
//        if (channel == null) {
//            System.out.println("해당 채널이 없습니다");
//            return false;
//        }
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return false;
//        User user = userOpt.get();
//        if (user.getStatus() == User.Status.INACTIVE) {
//            System.out.println("휴면중인 유저입니다");
//            return false;
//        }
//        // 유저의 채널리스트에 채널 추가
//        user.addChannel(channel);
//        user.newUpdatedAt();
//        repo.save(user);
//        FileChannelRepository.getInstance().saveAllChannels();
//        return true;
//    }
//
//    // 유저가 참여하고있는 채널 리스트 반환
//    @Override
//    public List<Channel> getChannelsList(UUID userId) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return new ArrayList<>();
//        return userOpt.get().getChannels();
//    }
//
//    // 해당 채널에서 퇴장
//    @Override
//    public boolean exitFromChannel(UUID userId, Channel channel) {
//        if (channel == null) {
//            System.out.println("해당 채널이 없습니다");
//            return false;
//        }
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return false;
//        User user = userOpt.get();
//        user.removeChannel(channel);
//        user.newUpdatedAt();
//        channel.newUpdatedAt();
//        repo.save(user);
//        FileChannelRepository.getInstance().saveAllChannels();
//        return true;
//    }
//
//    // 해당 유저의 상태 변경
//    @Override
//    public void setUserStatus(UUID userId, User.Status status) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return;
//        User user = userOpt.get();
//        if (status == User.Status.DELETED) {
//            System.out.println("상태 변경에서는 회원 탈퇴를 할 수 없습니다.");
//            return;
//        }
//        user.setStatus(status);
//        repo.save(user);
//    }
//
//    // 유저가 작성한 모든 채팅 리스트 반환
//    @Override
//    public List<Message> getMessageList(UUID userId) {
//        Optional<User> userOpt = findUserById(userId);
//        if (userOpt.isEmpty()) return new ArrayList<>();
//        return userOpt.get().getMessages();
//    }
//}
