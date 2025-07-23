//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.ActiveStatus;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//public class FileUserService implements UserService {
//
//    UserRepository userRepository;
//    ChannelRepository channelRepository;
//    MessageRepository messageRepository;
//    private boolean emailMatch;
//
//    public FileUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }
//
//    public void validatedUser(User user) {
//        if (!user.isActive().equals(ActiveStatus.ACTIVE)) new IllegalArgumentException("User is not active");
//    }
//
//    // 동일한 이메일이 존재하는지 확인
//    @Override
//    public boolean isDuplicateEmail(String email) {
//        return userRepository.findAll().stream()
//                .anyMatch(user -> user.getEmail().equals(email));
//    }
//
//    // 유저 생성
//    @Override
//    public User createUser(String newName, String password, String email) {
//        User user = new User(newName, password, email);
//        emailMatch = userRepository.findAll().stream().
//                anyMatch(host -> host.getEmail().equals(email));
//        if (!emailMatch) {
//            user.setActive(ActiveStatus.ACTIVE);
//            userRepository.save(user);
//        }
//        return user;
//    }
//
//    // 모든 유저 확인
//    @Override
//    public List<User> getUsers() {
//        return userRepository.findAll();
//    }
//
//    // 특정 ID를 가진 유저 가져오기
//    @Override
//    public User getUsersById(UUID userId) {
//        User user = userRepository.findById(userId);
//        return user;
//    }
//
//    /********************************************
//     * 유저 정보 업데이트
//     * @param user  유저의 아이디 / 수정되지 않음 / 유저를 찾는 용도
//     * @param updateUser 수정할 정보를 담은 User / null이 아닌 값을 기존 유저의 값을 수정
//     ********************************************/
//    @Override
//    public void updateUser(User user, User updateUser) {
//        User target = userRepository.findById(user.getId());
//        validatedUser(target);
//        if (updateUser.getUserName() != null) {
//            user.setUserName(updateUser.getUserName());
//        }
//        if (updateUser.getPassword() != null) {
//            user.setPassword(updateUser.getPassword());
//        }
//        if (updateUser.getEmail() != null) {
//            user.setEmail(updateUser.getEmail());
//        }
//        if (updateUser.getStatus() != null) {
//            user.setStatus(updateUser.getStatus());
//        }
//        if (updateUser.getUserName() != null || updateUser.getPassword() != null || updateUser.getStatus() != null) {
//            user.setUpdatedAt(System.currentTimeMillis());
//        }
//        userRepository.save(user);
//    }
//
//    /********************************************
//     *  유저 삭제 메서드
//     * @param user 아이디를 입력으로 받음
//     ********************************************/
//    @Override
//    public void deleteUser(User user) {
//        validatedUser(user);
//        /********************************************
//         * 유저가 있던 채널에서 유저 삭제
//         ********************************************/
//        user.getChannels().stream()
//                .forEach(channel -> {
//                    channel.getUsers().remove(user);
//                    channel.setUpdatedAt(System.currentTimeMillis());
//                    channelRepository.save(channel);
//                });
//        // 모든 채널 내 해당 유저 삭제
//
//        /********************************************
//         * 유저가 작성한 메시지를 전체 메시지 내역에서 삭제
//         ********************************************/
//        user.getMessages().stream()
//                .forEach(message -> {
//                    removeMessage(user,message);
//                });
//        // 해당 유저의 모든 대화 내역 삭제
//        userRepository.delete(user);
//        user.setActive(ActiveStatus.DELETE);
//    }
//
//    /**
//     * 채널 나가기
//     * 유저가 채널을 나가서 삭제되거나 채널이 유저를 퇴장해서 채널이 삭제될 때 사용
//     * @param user
//     * @param channel
//     */
//    public void leaveChannel(User user, Channel channel) {
//        validatedUser(user);
//        user.getMessages().stream()
//                .filter(message -> message.getChannelId().equals(channel.getId()))
//                .forEach(message -> {
//                    removeMessage(user, message);
//                });
//        user.getChannels().remove(channel);
//        userRepository.save(user);
//        channel.removeUser(user);
//        channelRepository.save(channel);
//    }
//
//    @Override
//    public void removeMessage(User user, Message message) {
//        if (user.getMessages().contains(message) && message.isActive().equals(ActiveStatus.ACTIVE)) {
//            Channel channel = message.getChannel();
//            user.removeMessage(message);
//            channel.removeMessage(message);
//            channelRepository.save(channel);
//            channel.setUpdatedAt(System.currentTimeMillis());
//            message.setActive(ActiveStatus.DELETE);
//            message.setUpdatedAt(System.currentTimeMillis());
//            messageRepository.delete(message);
//        }
//    }
//}
