package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.file.FileIOHelper;

import java.nio.file.Path;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private static FileChannelRepository instance;
    private final Map<UUID, Channel> data;
    private final Path filePath;

    private FileChannelRepository(Path filePath) {
        this.filePath = filePath;
        this.data = FileIOHelper.loadMap(filePath);
    }
    // 최초 초기화 시 Path 필요
    public static FileChannelRepository getInstance(Path filePath) {
        if (instance == null) {
            instance = new FileChannelRepository(filePath);
        }
        return instance;
    }

    // 초기화 후에는 경로 없이도 호출 가능
    public static FileChannelRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FileChannelRepository가 아직 초기화되지 않았습니다. getInstance(Path filePath)를 먼저 호출하세요.");
        }
        return instance;
    }

    public void saveAllChannels() {
        FileIOHelper.saveMap(filePath, data);
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getChannelId(), channel);
        saveAllChannels();
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        data.remove(channelId);
        saveAllChannels();
    }

    @Override
    public Channel findById(UUID channelId) {
        return data.get(channelId);
    }

    @Override
    public List<Channel> findByName(String channelName) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : data.values()) {
            if (channel.getChannelName().equals(channelName)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean isContains(UUID channelId) {
        return data.containsKey(channelId);
    }
}
