package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileChannelService implements ChannelService {
    private static FileChannelService instance;
    private final ChannelRepository repo;

    private FileChannelService() {
        repo = FileChannelRepository.getInstance();
    }

    public static FileChannelService getInstance() {
        if (instance == null) {
            instance = new FileChannelService();
        }
        return instance;
    }

    // 새로운 채널 등록
    @Override
    public Channel registChannel(String channelName) {
        if (channelName == null || channelName.isEmpty() || channelName.equals("*")) {
            System.out.println("잘못된 입력입니다.");
            return null;
        }
        Channel newChannel = new Channel(channelName);
        return repo.save(newChannel);
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId) {
        Optional<Channel> toDeleteOpt = findChannelById(channelId);
        if (toDeleteOpt.isEmpty()) {
            System.out.println("삭제할 채널이 없습니다.");
            return false;
        }
        Channel toDelete = toDeleteOpt.get();

        // 채널 내 메시지 모두 삭제
        List<Message> messagesCopy = new ArrayList<>(toDelete.getMessages());
        for (Message message : messagesCopy) {
            FileMessageService.getInstance().deleteMessage(message.getMessageId());
        }

        // 채널 내 유저들의 채널 연결 해제
        for (User user : new ArrayList<>(toDelete.getUsers())) {
            user.removeChannel(toDelete);
            user.newUpdatedAt();
            FileUserRepository.getInstance().saveAllUsers();
        }

        repo.delete(channelId);
        return true;
    }

    // 채널 정보 출력
    @Override
    public void showChannelInfo(Channel channel) {
        if (channel == null || !repo.isContains(channel.getChannelId())) {
            System.out.println("해당 채널이 없습니다");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = Instant.ofEpochMilli(channel.getCreatedAt())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String createdDate = dateTime.format(formatter);
        System.out.println("채널 이름: " + channel.getChannelName()
                + ", 채널 ID: " + channel.getChannelId()
                + ", 채널 생성 날짜: " + createdDate);
    }

    // UUID로 채널 검색 (Optional 적용)
    public Optional<Channel> findChannelById(UUID channelId) {
        Channel channel = repo.findById(channelId);
        if (channel == null) {
            System.out.println("존재하지 않는 채널입니다");
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    // 채널명으로 채널 검색(동일한 이름의 채널들 리스트 형태로 모두 반환)
    @Override
    public List<Channel> findChannelByName(String channelName) {
        if (channelName.equals("*")) {
            return repo.findAll();
        }
        List<Channel> channelByName = repo.findByName(channelName);
        if (channelByName.isEmpty()) {
            System.out.println("존재하지 않는 채널입니다");
        }
        return channelByName;
    }

    // 채널명 변경
    @Override
    public Channel updateChannelName(UUID channelId, String updateChannelName) {
        if (updateChannelName == null || updateChannelName.isEmpty()) {
            System.out.println("잘못된 입력입니다.");
            return null;
        }
        Optional<Channel> channelOpt = findChannelById(channelId);
        if (channelOpt.isEmpty()) return null;
        Channel channel = channelOpt.get();
        channel.updateChannelName(updateChannelName);
        channel.newUpdatedAt();
        repo.save(channel);
        return channel;
    }

    // 채널에 참여하고 있는 유저 리스트 반환
    @Override
    public List<User> getUserList(UUID channelId) {
        Optional<Channel> channelOpt = findChannelById(channelId);
        if (channelOpt.isEmpty()) return Collections.emptyList();
        Channel channel = channelOpt.get();
        List<User> usersInChannel = channel.getUsers();
        if (usersInChannel.isEmpty()) {
            System.out.println("해당 채널에 참여하고 있는 유저가 없습니다.");
        }
        return usersInChannel;
    }

    // 채널에 참여하고 있는 유저 강퇴
    @Override
    public boolean kickUser(UUID channelId, User user) {
        Optional<Channel> channelOpt = findChannelById(channelId);
        if (channelOpt.isEmpty()) return false;
        Channel channel = channelOpt.get();
        if (!channel.getUsers().contains(user)) {
            System.out.println("해당 유저는 채널에 없습니다.");
            return false;
        }
        channel.removeUser(user);
        user.newUpdatedAt();
        channel.newUpdatedAt();
        repo.save(channel);
        FileUserRepository.getInstance().saveAllUsers();
        return true;
    }

    // 채널 내 모든 메세지 리스트 반환
    @Override
    public List<Message> getMessageList(UUID channelId) {
        Optional<Channel> channelOpt = findChannelById(channelId);
        if (channelOpt.isEmpty()) return Collections.emptyList();
        Channel channel = channelOpt.get();
        List<Message> messages = channel.getMessages();
        if (messages.isEmpty()) {
            System.out.println("채널에 작성된 메세지가 없습니다.");
        }
        return messages;
    }
}
