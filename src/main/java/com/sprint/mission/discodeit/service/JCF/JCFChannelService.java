package com.sprint.mission.discodeit.service.JCF;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService  {

    //싱글톤으로 변경
    private static JCFChannelService instance;
    private final List<Channel> data;
    private final JCFMessageService messageService;

    private JCFChannelService(){
        data = new ArrayList<>();
        this.messageService = JCFMessageService.getInstance();
    }

    public static JCFChannelService getInstance() {
        if (instance == null) {
            instance = new JCFChannelService();
        }
        return instance;
    }

    //신규 채널 등록
    @Override
    public Channel registChannel(String channelName){
        if(channelName == null || channelName.isEmpty() || channelName.equals("*")) {
            //*은 모든 채널 검색에 사용되므로 채널 이름으로 사용 불가
            System.out.println("잘못된 입력입니다.");
            return null;
        }
        //채널 이름이 공백이 아닐경우 새 채널 생성
        Channel newChannel = new Channel(channelName);
        data.add(newChannel);
        return newChannel;
    }

    //채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel toDelete = findChannelById(channelId);
        List<Message> messagesCopy = new ArrayList<>(toDelete.getMessages());
        //반복문 도중 리스트를 직접 수정하면 ConcurrentModificationException이 발생. 복사본 사용
        for (Message message : messagesCopy) {
            messageService.deleteMessage(message.getMessageId());
        }
        for (User user : new ArrayList<>(toDelete.getUsers())) {
            user.removeChannel(toDelete);
            user.newUpdatedAt();
        }
        data.remove(toDelete);
        return true;
    }

    //선택한 채널 정보 출력
    @Override
    public void showChannelInfo(Channel channel) {
        if(channel == null || !data.contains(channel)) {
            System.out.println("해당 채널이 없습니다");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = Instant.ofEpochMilli(channel.getCreatedAt())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        String createdDate = dateTime.format(formatter);
        System.out.println("채널 이름: " + channel.getChannelName() + ", 채널 ID: " + channel.getChannelId() + ", 채널 생성 날짜: " + createdDate);
    }

    //channelId로 채널 검색
    public Channel findChannelById(UUID channelId){
        return data.stream()
                .filter(c -> c.getChannelId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다"));
    }

    //검색한 이름의 모든 채널 리스트 반환
    public List<Channel> findChannelByName(String channelName){
        if(channelName.equals("*")) {
            return data;
            //*입력시 모든 유저 반환
        }
        List<Channel> channelByName = data.stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .toList();

        if(channelByName.isEmpty()) {
            System.out.println("존재하지 않는 채널입니다");
        }
        return channelByName;
    }

    //채널 이름 수정
    public Channel updateChannelName(UUID channelId, String updateChannelName){
        if(updateChannelName == null || updateChannelName.isEmpty()) {
            System.out.println("잘못된 입력입니다.");
            return null;
        }

        Channel channel = findChannelById(channelId);
        channel.updateChannelName(updateChannelName);
        channel.newUpdatedAt();
        return channel;
    }

    //채널 내 모든 유저 리스트 조회
    public List<User> getUserList(UUID channelId){
        Channel selectedChannel = findChannelById(channelId);
        List<User> usersInChannel = selectedChannel.getUsers();
        if(usersInChannel.isEmpty()) {
            System.out.println("해당 채널에 참여하고 있는 유저가 없습니다.");
        }
        return usersInChannel;
    }

    //채널 내 유저 강퇴
    public boolean KickUser(UUID channelId, User user){
        Channel selectedChannel = findChannelById(channelId);
        selectedChannel.removeUser(user);
        user.newUpdatedAt();
        selectedChannel.newUpdatedAt();
        return true;
    }

    //채널 내 모든 메세지 기록 조회
    public List<Message> getMessageList(UUID channelId){
        Channel channel = findChannelById(channelId);
        List<Message> messages = channel.getMessages();
        if(messages.isEmpty()) {
            System.out.println("채널에 작성된 메세지가 없습니다.");
        }
        return messages;
    }
}
