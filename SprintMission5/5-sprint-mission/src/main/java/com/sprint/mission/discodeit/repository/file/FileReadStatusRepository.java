package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatusEntity> readStatusCache = new ConcurrentHashMap<>();
  private final File readStatusDir;

  public FileReadStatusRepository(String baseDirectory) {
    this.readStatusDir = new File(baseDirectory, "ReadStatus");
    if (!readStatusDir.exists()) {
      if (!readStatusDir.mkdirs()) {
        throw new RuntimeException("ReadStatus 디렉토리 생성 실패: " + readStatusDir.getAbsolutePath());
      }
    }
    loadAllReadStatusesToCache();
  }

  private void saveReadStatusToFile(ReadStatusEntity status) {
    File file = new File(readStatusDir, status.getId().toString() + ".ser");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(status);
    } catch (IOException e) {
      throw new RuntimeException("ReadStatus 저장 실패: " + e.getMessage(), e);
    }
  }

  private void loadAllReadStatusesToCache() {
    File[] files = readStatusDir.listFiles((dir, name) -> name.endsWith(".ser"));
    if (files == null) {
      return;
    }

    for (File file : files) {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        ReadStatusEntity status = (ReadStatusEntity) ois.readObject();
        readStatusCache.put(status.getId(), status);
      } catch (IOException | ClassNotFoundException e) {
        System.err.println("ReadStatus 로딩 실패: " + e.getMessage());
      }
    }
  }

  @Override
  public ReadStatusEntity save(ReadStatusEntity status) {
    readStatusCache.put(status.getId(), status);
    saveReadStatusToFile(status);
    return status;
  }

  @Override
  public Optional<ReadStatusEntity> findById(UUID id) {
    return Optional.ofNullable(readStatusCache.get(id));
  }

  @Override
  public List<ReadStatusEntity> findAll() {
    return new ArrayList<>(readStatusCache.values());
  }

  @Override
  public void delete(UUID id) {
    readStatusCache.remove(id);
    File file = new File(readStatusDir, id.toString() + ".ser");
    if (file.exists() && !file.delete()) {
      System.err.println("ReadStatus 파일 삭제 실패: " + file.getAbsolutePath());
    }
  }
}