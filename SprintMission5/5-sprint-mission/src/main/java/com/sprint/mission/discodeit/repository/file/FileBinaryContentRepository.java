package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContentEntity> binaryContentCache = new ConcurrentHashMap<>();
  private final File binaryContentDir;

  public FileBinaryContentRepository(String baseDirectory) {
    this.binaryContentDir = new File(baseDirectory, "BinaryContent");
    if (!binaryContentDir.exists()) {
      if (!binaryContentDir.mkdirs()) {
        throw new RuntimeException("디렉토리 생성 실패: " + binaryContentDir.getAbsolutePath());
      }
    }
    loadAllBinaryContentsToCache();
  }

  private void loadAllBinaryContentsToCache() {
    File[] files = binaryContentDir.listFiles((dir, name) -> name.endsWith(".ser"));

    if (files != null) {
      for (File file : files) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
          BinaryContentEntity binaryContentEntity = (BinaryContentEntity) ois.readObject();
          binaryContentCache.put(binaryContentEntity.getId(), binaryContentEntity);
        } catch (IOException | ClassNotFoundException e) {
          System.err.println("파일 로딩 실패: " + file.getName() + " - " + e.getMessage());
        }
      }
    }
  }

  private void saveBinaryContentToFile(BinaryContentEntity binaryContentEntity) {
    File file = new File(binaryContentDir, binaryContentEntity.getId() + ".ser");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(binaryContentEntity);
    } catch (IOException e) {
      throw new RuntimeException("BinaryContent 저장 실패: " + file.getAbsolutePath(), e);
    }
  }

  @Override
  public BinaryContentEntity save(BinaryContentEntity binaryContentEntity) {
    binaryContentCache.put(binaryContentEntity.getId(), binaryContentEntity);
    saveBinaryContentToFile(binaryContentEntity);
    return binaryContentEntity;
  }

  @Override
  public Optional<BinaryContentEntity> findById(UUID id) {
    return Optional.ofNullable(binaryContentCache.get(id));
  }

  @Override
  public List<BinaryContentEntity> findAll() {
    return new ArrayList<>(binaryContentCache.values());
  }

  @Override
  public void delete(UUID id) {
    BinaryContentEntity removed = binaryContentCache.remove(id);
    if (removed != null) {
      File file = new File(binaryContentDir, id + ".ser");
      if (file.exists() && !file.delete()) {
        System.err.println("파일 삭제 실패: " + file.getAbsolutePath());
      }
    }
  }
}