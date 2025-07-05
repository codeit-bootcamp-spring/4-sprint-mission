package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String DATA_FILE = "data/binaryContent.ser";
    private final Map<UUID, BinaryContent> binaryContentCache = new ConcurrentHashMap<>();
    private final String fileDirectory;

    public FileBinaryContentRepository(String fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadAllUserStatusToCache();
    }

    private void saveAllBinaryContentToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<BinaryContent> binaryContentList = new ArrayList<>(binaryContentCache.values());
            oos.writeObject(binaryContentList);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패" + e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllUserStatusToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("데이터 파일이 존재하지 않습니다: " + DATA_FILE);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<BinaryContent> binaryContentList = (List<BinaryContent>) ois.readObject();
            binaryContentList.forEach(
                    binaryContent -> binaryContentCache.put(binaryContent.getId(), binaryContent));
        } catch (IOException e) {
            System.err.println("파일 입출력 오류 발생: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("클래스를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContentCache.put(binaryContent.getId(), binaryContent);
        saveAllBinaryContentToFile();
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent binaryContent = binaryContentCache.get(id);
        if (binaryContent == null) {
            return Optional.empty();
        }
        return Optional.of(binaryContent);
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(binaryContentCache.values());
    }

    @Override
    public void delete(UUID id) {
        BinaryContent binaryContent = binaryContentCache.get(id);
        if (binaryContent != null) {
            binaryContentCache.remove(id);
            saveAllBinaryContentToFile();
        }
    }
}
