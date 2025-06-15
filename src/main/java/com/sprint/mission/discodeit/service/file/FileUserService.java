package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserService implements UserService {

    private static FileUserService fileUserService;

    public static FileUserService getInstance() {
        if (fileUserService == null) {
            fileUserService = new FileUserService();
        }
        return fileUserService;
    }

    @Override
    public User createUser(User user) {
        return saveUser(user);
    }

    @Override
    public void findAllUsers() {
        List<User> users = loadAllUsers();
        users.forEach(user -> System.out.println(user.toString()));
    }

    @Override
    public User findUserById(UUID id) {
        List<User> users = loadAllUsers();
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 없습니다."));
    }

    @Override
    public void updateUser(UUID id, String updateUser) {
        List<User> users = loadAllUsers();

        users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .ifPresent(user -> {
                    deleteUser(user);
                    user.setUserName(updateUser);
                    user.setUpdatedAt(System.currentTimeMillis());
                    saveUser(user);
                });
    }

    @Override
    public void deleteUser(User user) {
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\users\\" + user.getId() + ".ser");
        if (dir.exists()) {
            boolean deleted = dir.delete();
            if (!deleted) {
                System.err.println("유저 삭제 실패");
            }
        }
    }

    @Override
    public void joinChannel(User user, Channel channel) {
        FileChannelService fcs = FileChannelService.getInstance();

        user.addChannel(channel);
        channel.addUser(user);

        saveUser(user);
        fcs.saveChannel(channel);
    }

    @Override
    public void leaveChannel(User user, Channel channel) {
        FileChannelService fcs = FileChannelService.getInstance();

        user.removeChannel(channel);
        saveUser(user);

        channel.removeUser(user);
        fcs.saveChannel(channel);
    }

    public User saveUser(User user) {
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\users\\" + user.getId() + ".ser");
        if (dir.exists()) {
            boolean deleted = dir.delete();
            if (!deleted) {
                System.err.println("유저 삭제 실패");
            }
        }
        try (
                FileOutputStream fos = new FileOutputStream(dir);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\users");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        for (File file : files) {
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                users.add((User) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return users;
    }
}
