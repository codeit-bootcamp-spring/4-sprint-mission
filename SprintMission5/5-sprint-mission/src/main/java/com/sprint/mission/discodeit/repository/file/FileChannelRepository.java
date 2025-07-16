package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileChannelRepository implements ChannelRepository {

  private final Map<UUID, ChannelEntity> channelCache = new ConcurrentHashMap<>();
  private final File channelDir;

  public FileChannelRepository(String baseDirectory) {
    this.channelDir = new File(baseDirectory, "Channel");
    if (!channelDir.exists()) {
      if (!channelDir.mkdirs()) {
        throw new RuntimeException("디렉토리 생성 실패: " + channelDir.getAbsolutePath());
      }
    }
    loadAllChannelsToCache();
  }

  private void loadAllChannelsToCache() {
    File[] files = channelDir.listFiles((dir, name) -> name.endsWith(".ser"));
    if (files != null) {
      for (File file : files) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
          ChannelEntity entity = (ChannelEntity) ois.readObject();
          channelCache.put(entity.getId(), entity);
        } catch (IOException | ClassNotFoundException e) {
          System.err.println("채널 로딩 실패: " + file.getName() + " - " + e.getMessage());
        }
      }
    }
  }

  private void saveChannelToFile(ChannelEntity entity) {
    File file = new File(channelDir, entity.getId() + ".ser");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(entity);
    } catch (IOException e) {
      throw new RuntimeException("채널 저장 실패: " + file.getAbsolutePath(), e);
    }
  }

  @Override
  public ChannelEntity save(ChannelEntity channelEntity) {
    channelCache.put(channelEntity.getId(), channelEntity);
    saveChannelToFile(channelEntity);
    return channelEntity;
  }

  @Override
  public List<ChannelEntity> findAll() {
    return channelCache.values().stream()
        .filter(c -> c.getState() != ChannelState.DELETED)
        .toList();
  }

  @Override
  public Optional<ChannelEntity> findById(UUID id) {
    ChannelEntity c = channelCache.get(id);
    if (c == null || c.getState() == ChannelState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(c);
  }

  @Override
  public void delete(UUID id) {
    ChannelEntity cached = channelCache.get(id);
    if (cached != null) {
      cached.setToDelete();
      saveChannelToFile(cached);
    }
  }

  @Override
  public List<ChannelEntity> findChannelsByNameContains(String channelName) {
    return channelCache.values().stream()
        .filter(c -> c.getChannelName() != null && c.getChannelName().contains(channelName))
        .filter(c -> c.getState() != ChannelState.DELETED)
        .toList();
  }
}