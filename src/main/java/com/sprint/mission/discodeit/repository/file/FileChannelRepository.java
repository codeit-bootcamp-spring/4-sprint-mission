package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileChannelRepository implements ChannelRepository, Serializable {

    private static final String CHANNEL_FILE_PATH = "./data/channels.ser";
    private static final FileChannelRepository instance = new FileChannelRepository();

    public FileChannelRepository() {}
    public static FileChannelRepository getInstance() {
        return instance;
    }

    private List<Channel> loadChannels() {
        File file = new File(CHANNEL_FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void saveChannels(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CHANNEL_FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Channel createChannel(User user, String channelName) {
        if (user.getStatus() == UserStatus.DEACTIVE) return null;

        List<Channel> channels = loadChannels();
        Channel channel = new Channel(user, channelName);

        channel.addUser(user);
        channels.add(channel);
        
        saveChannels(channels);

        List<User> users = FileUserRepository.getInstance().getUsers();


        for (User userFromFile : users) {
            if(userFromFile.getId().equals(user.getId())) {
                userFromFile.addChannel(channel);
                break;
            }
        }

        FileUserRepository.getInstance().saveUsers(users); // 채널을 생성하면 자동으로 유저 정보도 재저장
        return channel;
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> ch.getId().equals(channel.getId()) && ch.getHostUser().getId().equals(user.getId()));

        // 유저 리스트에서 해당 유저 찾아서 채널 삭제 및 메시지 삭제
        List<User> users = FileUserRepository.getInstance().getUsers();
        users.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(userFromFile -> {
                    userFromFile.removeChannel(channel);
                    userFromFile.getMessages().removeIf(message -> message.getChannel().getId().equals(channel.getId()));
                });

        // 메시지 리스트에서 해당 채널과 관련된 메시지 모두 삭제 (removeIf 사용)
        List<Message> messages = FileMessageRepository.getInstance().getMessages();
        messages.removeIf(message -> message.getChannel().getId().equals(channel.getId()));

        saveChannels(channels);
        FileUserRepository.getInstance().saveUsers(users);
        FileMessageRepository.getInstance().saveMessages(messages);
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        for (Channel ch : channels) {
            if (ch.getId().equals(channel.getId()) &&
                    ch.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {

                ch.addUser(user);
                break;
            }
        }

        List<User> users = FileUserRepository.getInstance().getUsers();
        for (User userFromFile : users) {
            if(userFromFile.getId().equals(user.getId())) {
                userFromFile.addChannel(channel);
                break;
            }
        }

        saveChannels(channels);
        FileUserRepository.getInstance().saveUsers(users);
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> {
            if (ch.equals(channel)) {
                ch.removeUser(user);
                return ch.getUsers().isEmpty(); // 유저 다 나가면 삭제
            }
            return false;
        });

        List<User> users = FileUserRepository.getInstance().getUsers();
        for (User userFromFile : users) {
            if(userFromFile.getId().equals(user.getId())) {
                userFromFile.removeChannel(channel);
                break;
            }
        }

        saveChannels(channels);
        FileUserRepository.getInstance().saveUsers(users);
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.stream()
                .filter(ch -> ch.getId().equals(channel.getId()) && ch.getHostUser().getId().equals(user.getId()))
                .findFirst()
                .ifPresent(ch -> ch.updateChannelName(newName));

        List<User> users = FileUserRepository.getInstance().getUsers();
        users.stream()
                .filter(userFromFile -> userFromFile.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(userFromFile -> {
                    userFromFile.getChannels().removeIf(ch -> ch.getId().equals(channel.getId()));
                    userFromFile.getChannels().add(channel);
                });
        saveChannels(channels);
        FileUserRepository.getInstance().saveUsers(users);
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        if (channel == null || oldHostUser.getStatus() == UserStatus.DEACTIVE || newHostUser.getStatus() == UserStatus.DEACTIVE)
            return;

        List<Channel> channels = loadChannels();
        for (Channel ch : channels) {
            if (ch.equals(channel) && ch.getHostUser().equals(oldHostUser) && ch.getUsers().contains(newHostUser)) {
                ch.updateHostUser(newHostUser);
                break;
            }
        }

        List<User> users = FileUserRepository.getInstance().getUsers();
        for (User userFromFile : users) {
            if(userFromFile.getId().equals(oldHostUser.getId())) {
                userFromFile.getChannels().removeIf(ch -> ch.equals(channel));
                userFromFile.getChannels().add(channel);
                break;
            }
        }
        // 원하는 채널을 찾고 유저한테서 해당 채널을 지우고 정보가 바뀐 같은 채널을 유저한테 집어 넣음
        
        saveChannels(channels);
        FileUserRepository.getInstance().saveUsers(users);
    }

    @Override
    public List<Channel> getAllChannels() {
        return loadChannels();
    }

}
