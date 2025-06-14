package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileChannelRepository implements ChannelRepository {
    private static final String SERIALIZED_FILE_PATH = "data/channels.ser";

    @Override
    public List<Channel> findAll() {
        try (FileInputStream fis = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Channel findById(String channelid) {
        return findAll().stream()
                .filter(channel -> channel.getChannelId().equals(channelid))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = findAll();
        channels.add(channel);
        saveAll(channels);
        return channel;
    }

    private void saveAll(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZED_FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel deleteById(String channelId) {
        List<Channel> channels = findAll();
        Channel removed = null;

        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getChannelId().equals(channelId)) {
                removed = channels.remove(i);
                break;
            }
        }

        if (removed != null) {
            FileMessageRepository messageRepository = FileMessageRepository.getInstance();
            removed.getMessages().forEach(message -> {
                messageRepository.delete(message.getMessageId());
            });

            removed.getMessages().clear(); // 메시지 목록도 정리
            saveAll(channels);
        }

        return removed;
    }

}
