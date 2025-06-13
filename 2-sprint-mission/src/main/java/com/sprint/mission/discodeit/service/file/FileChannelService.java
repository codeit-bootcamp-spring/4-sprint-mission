package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {
    private static final String DATA_FILE = "data/channels.ser";
    private final Map<UUID, Channel> channelCache = new ConcurrentHashMap<>();

    public FileChannelService() {
        loadAllChannelsToCache();
    }

    public void saveChannel() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            List<Channel> channels = new ArrayList<>(channelCache.values());
            oos.writeObject(channels);
        }catch (IOException e){
            throw new RuntimeException("저장 오류: " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAllChannelsToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            List<Channel> channels = (List<Channel>) ois.readObject();
            channels.forEach(channel -> channelCache.put(channel.getChannelId(), channel));
        }catch (IOException | ClassNotFoundException e){
            System.err.println("불러오기 오류: " + e.getMessage());
        }
    }

    @Override
    public Channel createChannel(String name, User user) {
        Channel channel = new Channel(name, user);
        channelCache.put(channel.getChannelId(), channel);
        saveChannel();
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelCache.values().stream()
                .filter(channel -> channel.getState() != ChannelState.DELETED)
                .collect(Collectors.toList());
    }
    //optional로 바꾸기
    @Override
    public Optional<Channel> findChannel(UUID channelId) {
        Channel channel = channelCache.get(channelId);
        if(channel == null || channel.getState() == ChannelState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    @Override
    public List<Channel> findChannelsByNameContains(String channelName) {
        return channelCache.values().stream()
                .filter(channel -> channel.getChannelName().contains(channelName))
                .filter(channel -> channel.getState() != ChannelState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findMessagesByUserInChannel(Channel channel, User user) {
        return channel.getChannelMessages().stream()
                .filter(message -> message.getUser().equals(user))
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersInChannel(Channel channel) {
        return channelCache.values().stream()
                .filter(c -> c.getChannelId().equals(channel.getChannelId()))
                .flatMap(c -> c.getUsers().stream())
                .filter(user -> user.getState()!=UserState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findMessagesInChannel(Channel channel) {
        return channelCache.values().stream()
                .filter(c -> c.getChannelId().equals(channel.getChannelId()))
                .flatMap(c -> c.getChannelMessages().stream())
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }


    @Override
    public void updateChannelName(UUID ChannelId, String updatedText) {
        Channel channel = channelCache.get(ChannelId);
        if(channel != null && channel.getState() != ChannelState.DELETED) {
            channel.setChannelName(updatedText);
            channel.setUpdatedAt();
            saveChannel();
        }
    }
    @Override
    public void joinUser(Channel channel, User user) {
        Channel cached = channelCache.get(channel.getChannelId());
        if(cached != null && cached.getState() != ChannelState.DELETED) {
            cached.addUser(user);
            saveChannel();
        }
    }

    @Override
    public void leaveUser(Channel channel, User user) {
        Channel cached = channelCache.get(channel.getChannelId());
        if(cached != null && cached.getState() != ChannelState.DELETED) {
            cached.removeUser(user);
            saveChannel();
        }
    }

    @Override
    public void deleteChannel(Channel channel) {
        Channel cached = channelCache.get(channel.getChannelId());
        if(cached != null && cached.getState() != ChannelState.DELETED) {
            cached.deleteChannelState();
            saveChannel();
        }
    }

}
