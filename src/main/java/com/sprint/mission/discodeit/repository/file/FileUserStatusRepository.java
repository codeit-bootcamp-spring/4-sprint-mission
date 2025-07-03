package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class FileUserStatusRepository implements UserStatusRepository, Serializable {

    private final String filePath;

    public FileUserStatusRepository(String fileDirectory) {
        this.filePath = fileDirectory + "/userStatus.ser";
    }

    @Override
    public List<UserStatus> loadUserStatuses() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveStatuses(List<UserStatus> userStatuses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(userStatuses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUserStatus(UserStatus userStatus) {
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.add(userStatus);
        saveStatuses(userStatuses);
    }

    @Override
    public void deleteUserStatusByUserStatusId (UUID userStatusId){
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.removeIf(userStatus ->userStatus.equalsId(userStatusId));
        saveStatuses(userStatuses);
    }

    @Override
    public void deleteUserStatusByUserId (UUID userId){
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.removeIf(userStatus ->userStatus.getUserId().equals(userId));
        saveStatuses(userStatuses);
    }

    @Override
    public Optional<UserStatus> findUserStatusByUserId (UUID userId){
        List<UserStatus> userStatuses = loadUserStatuses();
        return userStatuses.stream()
                .filter(userStatus ->userStatus.getUserId().equals(userId)).findFirst();
    }

    @Override
    public Optional<UserStatus> findUserStatusByUserStatusId(UUID userStatusId){
        List<UserStatus> userStatuses = loadUserStatuses();
        return userStatuses.stream().filter(userStatus ->userStatus.getUserId().equals(userStatusId)).findFirst();
    }

    @Override
    public void updateUserStatus(UserStatus userStatus){
        List<UserStatus> userStatuses = loadUserStatuses();
        for (int i = 0; i < userStatuses.size(); i++) {
            if (userStatuses.get(i).equalsId(userStatus.getId())) {
                userStatuses.set(i, userStatus);
                saveStatuses(userStatuses);
                return;
            }
        }

    }
}
