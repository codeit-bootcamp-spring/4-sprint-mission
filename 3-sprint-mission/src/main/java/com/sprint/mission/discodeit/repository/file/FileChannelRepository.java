package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {
    private static final String DATA_FILE = "data/channel.ser";
    private final Map<UUID, Channel> channelCache = new ConcurrentHashMap<>();
    private final String fileDirectory;

    public FileChannelRepository(String fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadAllChannelsToCache();
    }

    public void saveAllChannelToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<Channel> channels = new ArrayList<>(channelCache.values());
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException("저장 오류: " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAllChannelsToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("데이터 파일이 존재하지 않습니다: " + DATA_FILE);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Channel> channels = (List<Channel>) ois.readObject();
            channels.forEach(channel -> channelCache.put(channel.getId(), channel));
        } catch (IOException e) {
            System.err.println("파일 입출력 오류 발생: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("클래스를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public Channel save(Channel channel) {
        channelCache.put(channel.getId(), channel);
        saveAllChannelToFile();
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return channelCache.values().stream()
                .filter(channel -> channel.getState() != ChannelState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = channelCache.get(id);
        if (channel == null || channel.getState() == ChannelState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel cached = channelCache.get(id);
        if (cached != null) {
            cached.deleteChannelState();
            saveAllChannelToFile();
        }
    }

    @Override
    public List<Channel> findChannelsByNameContains(String channelName) {
        return channelCache.values().stream()
                .filter(channel -> channel.getChannelName().contains(channelName))
                .collect(Collectors.toList());
    }
}
