//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.ActiveStatus;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//public class FileChannelService implements ChannelService {
//
//    private final ChannelRepository channelRepository;
//    private final UserRepository userRepository;
//    private final MessageRepository messageRepository;
//
//    public FileChannelService(ChannelRepository channelRepository, UserRepository userRepository, MessageRepository messageRepository) {
//        this.channelRepository = channelRepository;
//        this.userRepository = userRepository;
//        this.messageRepository = messageRepository;
//    }
//
//    public void validatedChannel(Channel channel) {
//        if (!channel.isActive().equals(ActiveStatus.ACTIVE)) throw new IllegalArgumentException("Channel is not active");
//    }
//
//    // 채널 생성자
//    @Override
//    public Channel createChannel(User user, String channelName) {
//        Channel channel = new Channel(user.getId(), channelName);
//        if (user.isActive().equals(ActiveStatus.ACTIVE)) {
//            addUserToChannel(channel, user);
//            channel.setActive(ActiveStatus.ACTIVE);
//            channelRepository.save(channel);
//            userRepository.save(user);
//        }
//        return channel;
//    }
//
//    // 현재 존재하는 모든 채널 확인
//    @Override
//    public List<Channel> getAllChannels() {
//        return channelRepository.findAll();
//    }
//
//
//    // 채널의 ID를 사용하여 채널 검색
//    @Override
//    public Channel getChannelById(UUID channelId) {
//        return channelRepository.findById(channelId);
//    }
//
//    // 채널 업데이트 ( 정보 수정 )
//    // 현재는 수정 가능한 입력이 channelName 1개
//    @Override
//    public void updateChannel(Channel channel, String channelName) {
//        validatedChannel(channel);
//        channel.setChannelName(channelName);
//        channel.setUpdatedAt(System.currentTimeMillis());
//        channelRepository.save(channel);
//    }
//
//    // 채널 삭제
//    @Override
//    public void deleteChannel(Channel channel) {
//        validatedChannel(channel);
//
//        /***********************************
//         * 채널을 가지고 있는 유저에게 채널 삭제
//         ***********************************/
//        channel.getUsers().stream()
//                .forEach(user -> {
//                    user.getChannels().remove(channel);
//                    user.setUpdatedAt(System.currentTimeMillis());
//                    userRepository.save(user);
//                });
//        // 모든 유저에게 해당 채널 삭제
//
//        /***********************************
//         * 전체 메시지 중 해당 채널의 메시지 삭제
//         ***********************************/
//        channel.getMessages().stream()
//                .forEach(message -> {
//                    removeMessage(channel,message);
//                });// 채널 내 모든 메세지 삭제
//        channelRepository.delete(channel);  // 전체 채널 리스트에서 해당 채널 삭제
//        channel.setActive(ActiveStatus.DELETE);
//    }
//
//    /***********************************
//     * 해당 채널의 존재하는 특정 유저를 id를 통해 삭제
//     * @param channel 삭제할 유저가 있는 채널
//     * @param userId 삭제할 유저의 id
//     ***********************************/
//    @Override
//    public void removeUserFromChannel(Channel channel, UUID userId) {
//        Optional<User> target = channel.getUsers().stream()
//                .filter(u -> u.getId().equals(userId))
//                .findFirst();
//        target.ifPresentOrElse(
//                user -> {
//                    user.removeChannel(channel);  // 유저에서 해당 채널을 삭제하고
//                    channel.setUpdatedAt(System.currentTimeMillis());  // 채널의 업데이트 시간 수정
//                    channelRepository.save(channel);
//                    userRepository.save(user);
//                },
//                () -> System.out.println("해당유저가 채널에 없습니다."));
//    }
//
//    /***********************************
//     * 해당 채널의 새로운 유저 추가
//     * @param channel 유저를 추가할 채널
//     * @param user 채널에 추가할 유저
//     ***********************************/
//    @Override
//    public void addUserToChannel(Channel channel, User user) {
//        if (user.isActive().equals(ActiveStatus.ACTIVE)) {
//            channel.addUser(user);  // 해당 유저에 채널을 추가하고
//            channel.setUpdatedAt(System.currentTimeMillis());  // 채널의 업데이트 시간 수정
//            userRepository.save(user);
//            channelRepository.save(channel);
//        }
//    }
//
//    @Override
//    public void removeMessage(Channel channel, Message message) {
//        if (messageRepository.findAll().contains(message) && channel.getMessages().contains(message)) {
//            User sender = message.getUser();
//            sender.removeMessage(message);
//            channel.removeMessage(message);
//            userRepository.save(sender);
//            channelRepository.save(channel);
//            message.setActive(ActiveStatus.DELETE);
//            message.setUpdatedAt(System.currentTimeMillis());
//            messageRepository.delete(message);
//        }
//    }
//}
