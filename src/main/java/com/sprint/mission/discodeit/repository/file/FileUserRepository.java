package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static FileUserRepository fileUserRepository;

    public static FileUserRepository getInstance() {
        if (fileUserRepository == null) {
            fileUserRepository = new FileUserRepository();
        }
        return fileUserRepository;
    }

    @Override
    public User saveUser(Path dir, User user) {
        if (Files.notExists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {}
        }
        Path filePath = dir.resolve(user.getUserName() + ".ser");
        if (Files.exists(filePath)) {
            throw new RuntimeException("해당 닉네임의 유저가 이미 존재합니다.");
        }
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public List<User> loadAllUsers(Path dir) {
        if (Files.exists(dir)) {
            try {
                List<User> users = Files.list(dir)
                        .map(path -> {
                            try(
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                User data = (User)(ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<User> loadUserById(Path dir, UUID id) {
        if  (Files.exists(dir)) {
            try {
                Optional<User> user = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                User data = (User)(ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(u -> u.getId().equals(id))
                        .findFirst();
                return user;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
        return Optional.empty();
        }
    }

    @Override
    public Optional<User> loadUserByUserName(Path dir, String userName) {
        if (Files.exists(dir)) {
            try {
                Optional<User> user = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                User data = (User)(ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(u -> u.getUserName().equals(userName))
                        .findFirst();
                return user;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(Path dir, User user) {
        loadUserById(dir, user.getId()).ifPresent(u -> {
            Path filePath = dir.resolve(u.getUserName() + ".ser");
            Path exFilePath = dir.resolve(user.getUserName() + ".ser");
            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(exFilePath);
            } catch (IOException e) {
                throw new RuntimeException(filePath + "파일 삭제 실패", e);
            }
        });
    }

    @Override
    public void joinChannel(Path userDir, Path channelDir, User user, Channel channel) {
        FileChannelRepository channelRepository = FileChannelRepository.getInstance();
        deleteUser(userDir, user);
        channelRepository.deleteChannel(channelDir, channel);

        user.addChannel(channel);
        channel.addUser(user);

        saveUser(userDir, user);
        channelRepository.saveChannel(channelDir, channel);
    }

    @Override
    public void leaveChannel(Path userDir, Path channelDir, User user, Channel channel) {
        FileChannelRepository channelRepository = FileChannelRepository.getInstance();
        deleteUser(userDir, user);
        channelRepository.deleteChannel(channelDir, channel);

        user.removeChannel(channel);
        channel.removeUser(user);

        saveUser(userDir, user);
        channelRepository.saveChannel(channelDir, channel);
    }
}
