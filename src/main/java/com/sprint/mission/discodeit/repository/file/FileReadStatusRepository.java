package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    @Value("${discodeit.repository.file-directory}/ReadUsers.ser")
    private String FILE_PATH;

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            list = (List<ReadStatus>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void saveAll(List<ReadStatus> readStatus) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(ReadStatus readStatus) {
        List<ReadStatus> list = findAll();
        if (list.stream().anyMatch(r -> r.getId().equals(readStatus.getId()))) {
            List<ReadStatus> updateList = list.stream()
                    .map(r -> r.getId().equals(readStatus.getId()) ? readStatus : r)
                    .collect(Collectors.toList());
            saveAll(updateList);
        } else {
            list.add(readStatus);
            saveAll(list);
        }
    }

    @Override
    public ReadStatus findById(UUID id) {
        List<ReadStatus> list = findAll();
        return list.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READSTATUS_NOT_FOUND));
    }

    @Override
    public void delete(UUID id) {
        List<ReadStatus> list = findAll();
        list.removeIf(r -> r.getId().equals(id));
        saveAll(list);
    }

    @Override
    public ReadStatus findByChannelIdAndUserId(UUID channelId, UUID userId) {
        ReadStatus findReadStatus = findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId) && readStatus.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new BusinessLogicException(ExceptionCode.READSTATUS_NOT_FOUND));
        return findReadStatus;
    }

    @Override
    public List<UUID> findByChannelId(UUID channelId) {
        List<UUID> list = new ArrayList<>();
        findAll().stream()
                .filter(r -> r.getChannelId().equals(channelId))
                .forEach(r -> list.add(r.getId()));
        return list;
    }
}
