package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String DATA_FILE = "data/readStatus.ser";
    private final Map<UUID, ReadStatus> readStatusCache = new ConcurrentHashMap<>();
    private final String fileDirectory;

    public FileReadStatusRepository(String fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadAllReadStatusFromCache();
    }

    private void saveAllReadStatusToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<ReadStatus> readStatusList = new ArrayList<>(readStatusCache.values());
            oos.writeObject(readStatusList);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패" + e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllReadStatusFromCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("데이터 파일이 존재하지 않습니다: " + DATA_FILE);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<ReadStatus> readStatusList = (List<ReadStatus>) ois.readObject();
            readStatusList.forEach(readStatus -> readStatusCache.put(readStatus.getId(), readStatus));
        } catch (IOException e) {
            System.err.println("파일 입출력 오류 발생: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("클래스를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusCache.put(readStatus.getId(), readStatus);
        saveAllReadStatusToFile();
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        ReadStatus readStatus = readStatusCache.get(id);
        if (readStatus != null) {
            readStatusCache.remove(id);
            saveAllReadStatusToFile();
        }
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatusCache.values());
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatus = readStatusCache.get(id);
        if (readStatus == null) {
            return Optional.empty();
        }
        return Optional.of(readStatus);
    }
}
