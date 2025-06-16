package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.ArrayList;

public class FileChannelService implements ChannelService {

    private static final FileChannelService instance = new FileChannelService();
    private final ArrayList<Channel> data;

    public FileChannelService() {
        this.data = loadChannels();
    }
    public ArrayList<Channel> getChannels() {
        return data;
    }

    public static FileChannelService getInstance() {
        return instance;
    }

    public void saveChannels(){
        System.out.println("채널 리스트 저장");
        String filePath = "./data/channels.ser";

        // ArrayList<Channel> 직렬화및 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
            System.out.println("Channel 리스트가 직렬화되어 '" + filePath + "' 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUserService.getInstance().saveUsers();
    }

    public ArrayList<Channel> loadChannels(){
        ArrayList<Channel> deserializedChannels = null;
        System.out.println("채널 리스트 불러오기");
        String filePath = "./data/channels.ser"; // 유저 직렬화

        if (!new File(filePath).exists() || new File(filePath).length() == 0) {
            return new ArrayList<Channel>(); // 해당 파일이 없으면 빈 ArrayList를 반환
        }

        // ArrayList<Channel> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            deserializedChannels = (ArrayList<Channel>) ois.readObject();

            /*
            System.out.println("역직렬화된 Channel 리스트 정보:");

            for (Channel channel : deserializedChannels) {
                System.out.println("------------");
                System.out.println("Channel: " + channel.toString());
            }*/

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deserializedChannels;
    }

    @Override
    public Channel createChannel(User user, String channelName) {
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 비활성 상태이므로 채널을 생성할 수 없습니다.", user.getUserName());
            return null;
        }
        Channel channel = new Channel(user, channelName);
        data.add(channel);

        System.out.printf("채널 생성 - 채널 주인: %s, 채널 이름: %s, 채널 ID: %s%n",
                user.getId(), channelName, channel.getId());
        user.addChannel(channel);

        saveChannels();
        return channel;
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 비활성 상태이므로 채널에 입장 할 수 없습니다.", user.getUserName());
            return; // 메서드 종료
        }
        if(channel.getUsers().contains(user)) {
            System.out.printf("'%s'는 '%s' 채널에 이미 존재합니다.", user.getUserName(), channel.getChannelName());
            return;
        }
        System.out.printf("'%s' 에 '%s' 이 입장했습니다.%n", channel.getChannelName(), user.getUserName());
        channel.addUser(user);
        user.addChannel(channel);

        saveChannels();
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 비활성 상태 입니다.", user.getUserName());
            return; // 메서드 종료
        }
        if(!channel.getUsers().contains(user)) {
            System.out.printf("'%s'는 '%s' 채널에 존재 하지 않아서 퇴장할 수 없습니다.%n", user.getUserName(), channel.getChannelName());
            return; // 메서드 종료
        }
        System.out.printf("'%s' 유저가 '%s' 채널을 떠났습니다.%n", user.getUserName(), channel.getChannelName());
        channel.removeUser(user);
        user.removeChannel(channel);
        if (channel.getUsers().isEmpty()) {
            System.out.printf("'%s' 채널은 유저 수가 0이므로 삭제합니다.%n", channel.getChannelName());
            data.remove(channel);
        }
        saveChannels();
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 비활성 상태이므로 채널의 이름을 변경할 수 없습니다.%n", user.getUserName());
            return; // 메서드 종료
        }
        if (channel.getHostUser().equals(user)) {
            System.out.printf("'%s' 채널의 이름이 '%s' 으로 변경되었습니다.%n",
                    channel.getChannelName(), newName);
            channel.updateChannelName(newName);
        } else {
            System.out.printf("'%s' 은 '%s' 채널 주인이 아닙니다.%n",
                    user.getUserName(), channel.getChannelName());
        }
        saveChannels();
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 이미 비활성 상태입니다. 따라서 '%s' 채널을 삭제할 수 없습니다.%n", user.getUserName(), channel.getChannelName());
            return; // 메서드 종료
        }
        if(!channel.getUsers().contains(user)) {
            System.out.printf("'%s'는 '%s' 채널에 존재하지 않아서 권한이 없습니다.%n", user.getUserName(), channel.getChannelName());
            return;
        }

        if (!user.equals(channel.getHostUser())) {
            System.out.printf("'%s'는 '%s' 채널의 주인이 아니어서 지울 수 있는 권한이 없습니다.%n", user.getUserName(), channel.getChannelName());
            return;
        }

        System.out.printf("채널 삭제: %s%n", channel.getChannelName());
        channel.getMessages()
                .stream()
                .map(message -> {
                    message.getUser().removeMessage(message);
                    return message;
                });

        channel.getUsers()
                .stream()
                .map(u -> {
                    u.removeChannel(channel);
                    return u;
                });

        channel.clearUsers();
        channel.clearMessages();
        data.remove(channel);

        saveChannels();
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        if(oldHostUser.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 비활성 상태이므로 권한이 없습니다.%n", oldHostUser.getUserName());
            return;
        }

        if(newHostUser.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s'는 비활성 상태이므로 권한이 없습니다.%n", newHostUser.getUserName());
            return;
        }

        if(!channel.getUsers().contains(newHostUser)) {
            System.out.printf("'%s'는 '%s' 채널에 존재하지 않아서 권한이 없습니다.%n", newHostUser.getUserName(), channel.getChannelName());
            return;
        }

        if (!channel.getHostUser().equals(oldHostUser)) {
            System.out.printf("'%s' 은 '%s' 채널 주인이 아닙니다.%n",
                    oldHostUser.getUserName(), channel.getChannelName());
            return;
        }

        System.out.printf("'%s' 채널 주인을 변경합니다. 새 주인: '%s'%n",
                channel.getChannelName(), newHostUser.getUserName());
        channel.updateHostUser(newHostUser);

        saveChannels();
    }

    @Override
    public void printAllChannels() {
        System.out.printf("전체 채널 조회, 채널 수: %d%n", data.size());
        data.forEach(channel -> System.out.println(channel.getChannelName()));
    }

    @Override
    public void printChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("채널 단일 조회, 채널 이름: %s, 채널 내 유저 수 %d%n", channel.getChannelName(), channel.getUsers().size());
        System.out.printf("채절 주인: '%s', 채널 내 유저 들=%s%n", channel.getHostUser().getUserName(), channel.getUsers());
        //System.out.println(channel.toString());
    }

    @Override
    public void printUsersFromChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("%s 채널 내 유저 검색, 채널 내 유저 수: %d%n",
                channel.getChannelName(), channel.getUsers().size());

        channel.getUsers().forEach(u -> System.out.println(u.getUserName()));
    }

}
