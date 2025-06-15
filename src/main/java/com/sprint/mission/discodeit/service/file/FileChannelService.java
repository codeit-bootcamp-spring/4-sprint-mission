package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private static FileChannelService fileChannelService;

    public static FileChannelService getInstance(){
        if(fileChannelService == null){
            fileChannelService = new FileChannelService();
        }
        return fileChannelService;
    }

    @Override
    public Channel createChannel(Channel channel) {
        return saveChannel(channel);
    }

    @Override
    public void findAllChannels() {
        List<Channel> channels = loadAllChannels();
        channels.forEach(channel -> System.out.println(channel.toString()));
    }

    @Override
    public Channel findChannelById(UUID id) {
        List<Channel> channels = loadAllChannels();
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 채널이 없습니다."));
    }

    @Override
    public void updateChannel(UUID id, String updateChannelName) {
        List<Channel> channels = loadAllChannels();
        channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .ifPresent(channel -> {
                    deleteChannel(channel);
                    channel.setChannelName(updateChannelName);
                    channel.setUpdatedAt(System.currentTimeMillis());
                    saveChannel(channel);
                });
    }

    @Override
    public void deleteChannel(Channel channel) {
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\channels\\" + channel.getId() + ".ser");
        if (dir.exists()) {
            boolean deleted = dir.delete();
            if (!deleted) {
                System.err.println("채널 삭제 실패");
            }
        }
    }
    public Channel saveChannel(Channel channel) {
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\channels\\" + channel.getId() + ".ser");
        if (dir.exists()) {
            boolean deleted = dir.delete();
            if (!deleted) {
                System.err.println("채널 삭제 실패");
            }
        }
        try (
                FileOutputStream fos = new FileOutputStream(dir);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public List<Channel> loadAllChannels() {
        List<Channel> channels = new ArrayList<>();
        File dir = new File("C:\\Users\\TAESANCHO\\codeit-mission\\1-sprint-mission" +
                "\\src\\main\\java\\com\\sprint\\mission\\discodeit" +
                "\\data\\channels");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        for (File file : files) {
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                channels.add((Channel) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return channels;
    }
}
