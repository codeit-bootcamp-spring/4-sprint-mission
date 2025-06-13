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

  public FileChannelRepository() {
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
      return;
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      List<Channel> channels = (List<Channel>) ois.readObject();
      channels.forEach(channel -> channelCache.put(channel.getChannelId(), channel));
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("불러오기 오류: " + e.getMessage());
    }
  }

  @Override
  public Channel save(Channel channel) {
    channelCache.put(channel.getChannelId(), channel);
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
  public void delete(Channel channel) {
    Channel cached = channelCache.get(channel.getChannelId());
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
