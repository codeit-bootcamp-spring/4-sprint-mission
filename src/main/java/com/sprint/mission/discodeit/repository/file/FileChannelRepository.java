package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static FileChannelRepository fileChannelRepository;

    public static FileChannelRepository getInstance() {
        if (fileChannelRepository == null) {
            fileChannelRepository = new FileChannelRepository();
        }
        return fileChannelRepository;
    }

    @Override
    public Channel saveChannel(Path dir, Channel channel) {
        if (Files.notExists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {}
        }
        Path filePath = dir.resolve(channel.getChannelName() + ".ser");
        if (Files.exists(filePath)) {
            throw new RuntimeException("해당 이름의 채널이 이미 존재합니다.");
        }
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;

    }

    @Override
    public List<Channel> loadAllChannels(Path dir) {
        if (Files.exists(dir)) {
            try {
                List<Channel> channels = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                Channel data = (Channel)(ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return channels;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Channel> loadChannelById(Path dir, UUID id) {
        if  (Files.exists(dir)) {
            try {
                Optional<Channel> channel = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                Channel data = (Channel) (ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(c -> c.getId().equals(id))
                        .findFirst();
                return channel;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Channel> loadChannelByChannelName(Path dir, String channelName) {
        if (Files.exists(dir)) {
            try {
                Optional<Channel> channel = Files.list(dir)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ) {
                                Channel data = (Channel)(ois.readObject());
                                return data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(c -> c.getChannelName().equals(channelName))
                        .findFirst();
                return channel;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteChannel(Path dir, Channel channel) {

        loadChannelById(dir, channel.getId()).ifPresent(c -> {
            Path filePath = dir.resolve(c.getChannelName() + ".ser");
            Path exFilePath = dir.resolve(channel.getChannelName() + ".ser");
            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(exFilePath);
            } catch (IOException e) {
                throw new RuntimeException(filePath + "파일 삭제 실패", e);
            }
        });
    }
}
