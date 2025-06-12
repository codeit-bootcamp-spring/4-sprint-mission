package com.sprint.mission.discodeit.service.JCF;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JCFChannelService implements ChannelService  {

    //싱글톤으로 변경
    private static JCFChannelService instance;
    private final JCFMessageService messageService;
    private final ChannelRepository repo;
    //채널 삭제시 메세지도 같이 삭제되기 때문에 사용

    private JCFChannelService(){
        repo = JCFChannelRepository.getInstance();
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
        return repo.save(newChannel);
    }

    //채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId) {
        Optional<Channel> toDeleteOpt = findChannelById(channelId);
        if (toDeleteOpt.isEmpty()) return false;
        Channel toDelete = toDeleteOpt.get();

        List<Message> messagesCopy = new ArrayList<>(toDelete.getMessages());
        //반복문 도중 리스트를 직접 수정하면 ConcurrentModificationException이 발생. 복사본 사용
        for (Message message : messagesCopy) {
            messageService.deleteMessage(message.getMessageId());
            //채널이 삭제되면 채널 내 메세지도 전부 삭제된다
            //복사본 순회하며 삭제하는 채널 내 메세지 삭제
        }
        for (User user : new ArrayList<>(toDelete.getUsers())) {
            user.removeChannel(toDelete);
            user.newUpdatedAt();
        }
        repo.delete(channelId);
        return true;
    }

    //선택한 채널 정보 출력
    @Override
    public void showChannelInfo(Channel channel) {
        if(channel == null || !repo.isContains(channel.getChannelId())) {
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

    //channelId로 채널 검색 (Optional 적용)
    public Optional<Channel> findChannelById(UUID channelId){
        Channel channel = repo.findById(channelId);
        if (channel == null) {
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    //검색한 이름의 모든 채널 리스트 반환
    public List<Channel> findChannelByName(String channelName){
        if(channelName.equals("*")) {
            return repo.findAll();
            //*입력시 모든 유저 반환
        }
        List<Channel> channelByName = repo.findByName(channelName);

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
        Optional<Channel> channelOpt = findChannelById(channelId);
        if (channelOpt.isEmpty()) return null;
        Channel channel = channelOpt.get();
        channel.updateChannelName(updateChannelName);
        channel.newUpdatedAt();
        return channel;
    }

    //채널 내 모든 유저 리스트 조회
    public List<User> getUserList(UUID channelId){
        Optional<Channel> selectedChannelOpt = findChannelById(channelId);
        if (selectedChannelOpt.isEmpty()) return Collections.emptyList();
        Channel selectedChannel = selectedChannelOpt.get();
        List<User> usersInChannel = selectedChannel.getUsers();
        if(usersInChannel.isEmpty()) {
            System.out.println("해당 채널에 참여하고 있는 유저가 없습니다.");
        }
        return usersInChannel;
    }

    //채널 내 유저 강퇴
    public boolean kickUser(UUID channelId, User user){
        Optional<Channel> selectedChannelOpt = findChannelById(channelId);
        if (selectedChannelOpt.isEmpty()) return false;
        Channel selectedChannel = selectedChannelOpt.get();
        selectedChannel.removeUser(user);
        user.newUpdatedAt();
        selectedChannel.newUpdatedAt();
        return true;
    }

    //채널 내 모든 메세지 기록 조회
    public List<Message> getMessageList(UUID channelId){
        Optional<Channel> channelOpt = findChannelById(channelId);
        if (channelOpt.isEmpty()) return Collections.emptyList();
        Channel channel = channelOpt.get();
        List<Message> messages = channel.getMessages();
        if(messages.isEmpty()) {
            System.out.println("채널에 작성된 메세지가 없습니다.");
        }
        return messages;
    }
}
