package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH = "data/channels.ser";

    @Override
    public Optional<Channel> findById(UUID channelId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<Channel> channels = (List<Channel>) ois.readObject();
            return channels.stream()
                    .filter(channel -> channel.getChannelId().equals(channelId))
                    .findFirst();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return java.util.Optional.empty();
        }
    }

    public void save(Channel channel) {
        List<Channel> channels;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            channels = (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            channels = new ArrayList<>();
        }
        channels.removeIf(ch -> ch.getChannelId().equals(channel.getChannelId()));
        channels.add(channel);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        List<Channel> channels;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            channels = (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            channels = new ArrayList<>();
        }

        channels.removeIf(ch -> ch.getChannelId().equals(channelId));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Channel> findAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
