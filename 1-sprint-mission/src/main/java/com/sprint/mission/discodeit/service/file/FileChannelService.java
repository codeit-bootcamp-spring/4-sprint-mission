package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService, Serializable {

    private final Map<String, Channel> data;
    private static final FileChannelService instance = new FileChannelService();
    private static final String FILE_PATH = "study/channels.txt";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SERIALIZED_FILE_PATH = "study/channels.ser";


    public FileChannelService() {
        this.data = new HashMap<>();
    }

    public static FileChannelService getInstance() {
        return instance;
    }

    //영속화를 위한 모든 채널을 찾는 메소드
    public Set<Channel> findAllChannels() {
        Set<Channel> channels = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();  //헤더 스킵

            while ((line = reader.readLine()) != null) {
                Channel channel = Channel.fromCSV(line);

                if (channel != null) {
                    channels.add(channel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return channels;
    }

    //영속화를 위한 모든 채널을 저장하는 메소드
    public void saveAllChannels(List<Channel> channels) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("name,description,created_at" + LINE_SEPARATOR);

            for (Channel channel : channels) {
                writer.write(channel.toCSV() + LINE_SEPARATOR);
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //직렬화 저장 메소드
    public void saveSerializedChannels(List<Channel> channels) {
        try (FileOutputStream fos = new FileOutputStream(SERIALIZED_FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //역직렬화 로드 메소드
    public List<Channel> loadSerializedChannels() {

        List<Channel> channels = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            channels = (List<Channel>) ois.readObject();

            data.clear();
            for (Channel channel : channels) {
                data.put(channel.getChannelId(), channel);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return channels;
    }

    //채널 생성
    @Override
    public Channel createChannel(Channel channel) {
        Set<Channel> channels = this.getAllChannels();

        channels.add(channel);
        List<Channel> list = new ArrayList<>(channels);

        //CVS 저장
        saveAllChannels(list);

        //직렬화 저장
        saveSerializedChannels(list);

        return channel;
    }

    //채널 조회
    @Override
    public Set<Channel> getAllChannels() {
        Map<String, Channel> resultMap = new HashMap<>();

        List<Channel> serializedChannels = loadSerializedChannels();
        if (serializedChannels != null) {
            for (Channel channel : serializedChannels) {
                resultMap.put(channel.getChannelId(), channel);
            }
        }

        Set<Channel> csvChannels = findAllChannels();
        for (Channel channel : csvChannels) {
            resultMap.put(channel.getChannelId(), channel);
        }

        data.clear();
        data.putAll(resultMap);

        return new HashSet<>(resultMap.values());
    }

    @Override
    public Channel getChannelById(String id) {

        getAllChannels();

        return data.get(id);
    }

    @Override
    public Channel updateChannel(String channelId, String newName, String newDescription) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found");
        } else {
            channel.setChannelname(newName);
            channel.setDescription(newDescription);
            channel.setUpdatedAt(System.currentTimeMillis());

            ArrayList<Channel> updated = new ArrayList<>(data.values());

            saveAllChannels(updated);

            saveSerializedChannels(updated);

            return channel;
        }
    }

    @Override
    public Channel deleteChannel(String channelId) {
        Channel removed = data.remove(channelId);
        if (removed != null) {
            removed.getMessages().clear();

            List<Channel> channelList = new ArrayList<>(data.values());
            saveAllChannels(channelList);

            saveSerializedChannels(channelList);
        }
        return removed;
    }
}