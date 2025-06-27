package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileReadStatusRepository implements ReadStatusRepository, Serializable {

    private final String filePath;

    public FileReadStatusRepository(String fileDirectory) {
        this.filePath = fileDirectory + "/readStatus.ser";
    }


    @Override
    public List<ReadStatus> loadReadStatuses() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ReadStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveReadStatuses(List<ReadStatus> readStatuses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(readStatuses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createReadStatus(ReadStatus readStatus) {
        List<ReadStatus> readStatuses = loadReadStatuses();
        readStatuses.add(readStatus);
        saveReadStatuses(readStatuses);
    }

    @Override
    public List<ReadStatus> findReadStatusesByUserId(UUID userId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)).toList();
    }

    @Override
    public List<ReadStatus> findReadStatusesByChannelId(UUID channelId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId)).toList();
    }

    @Override
    public void deleteReadStatus(UUID userId, UUID channelId){
        loadReadStatuses().removeIf(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId));
        saveReadStatuses(loadReadStatuses());
    }

    @Override
    public Optional<ReadStatus> findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId)).findFirst();
    }

    @Override
    public Optional<ReadStatus> findReadStatusesByReadStatusId(UUID readStatusId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getId().equals(readStatusId))
                .findFirst();

    }

    @Override
    public void updateReadStatus(ReadStatus readStatus){
        List<ReadStatus> readStatusesFromFile = loadReadStatuses();
        for (int i = 0; i < readStatusesFromFile.size(); i++) {
            if (readStatusesFromFile.get(i).equalsId(readStatus)) {
                readStatusesFromFile.set(i, readStatus); // 리스트 내부 요소를 직접 교체
                saveReadStatuses(readStatusesFromFile);
                return;
            }
        }
    }

    @Override
    public void deleteReadStatusByChannelId(UUID channelId){
        List<ReadStatus> readStatusesFromFile = loadReadStatuses();
        readStatusesFromFile.removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
        saveReadStatuses(readStatusesFromFile);
    }

    @Override
    public void deleteReadStatusByReadStatusId(UUID readStatusId){
        List<ReadStatus> readStatusesFromFile = loadReadStatuses();
        readStatusesFromFile.removeIf(readStatus -> readStatus.equalsId(readStatusId));
        saveReadStatuses(readStatusesFromFile);
    }
}
