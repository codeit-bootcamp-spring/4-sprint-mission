package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    @Value("${discodeit.repository.file-directory}/UserStatuses.ser")
    private String FILE_PATH;

    @Override
    public List<UserStatus> findAll() {
        List<UserStatus> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            list = (List<UserStatus>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void saveAll(List<UserStatus> userStatus) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(UUID id) {
        List<UserStatus> list = findAll();
        list.removeIf(userStatus -> userStatus.getId().equals(id));
        saveAll(list);
    }

    @Override
    public UserStatus findById(UUID id) {
        List<UserStatus> list = findAll();
        return list.stream()
                .filter(userStatus -> userStatus.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USERSTATUS_NOT_FOUND));
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        List<UserStatus> list = findAll();
        return list.stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    @Override
    public void save(UserStatus userStatus) {
        List<UserStatus> list = findAll();
        if (list.stream()
                .anyMatch(us -> us.getId().equals(userStatus.getId()))) {
            List<UserStatus> updateList = list.stream()
                    .map(us -> us.equals(userStatus) ? userStatus : us)
                    .collect(Collectors.toList());
            saveAll(updateList);
        } else {
            list.add(userStatus);
            saveAll(list);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UserStatus> list = findAll();
        list.removeIf(userStatus -> userStatus.getUserId().equals(userId));
        saveAll(list);
    }
}