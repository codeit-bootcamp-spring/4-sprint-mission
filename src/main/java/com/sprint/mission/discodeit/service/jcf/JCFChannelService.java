package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/********************************************
 * 채널 서비스 인터페이스 구현체
 * CRUD 실행
 * 채널 내 유저 추가 및 삭제
 * 2025.05.30 김민수
 * *
 * 코드 리팩토링 2025. 06. 02 김민수
 ********************************************/
public class JCFChannelService implements ChannelService {
    MessageService jcfMessageService;

    private final List<Channel> data;

    public JCFChannelService(MessageService jcfMessageService) {
        this.data = new ArrayList<>();
        this.jcfMessageService = jcfMessageService;
    }

    // 채널 생성
    @Override
    public Channel createChannel(User user, String channelName) {
        Channel channel = new Channel(user, channelName);
        if (user.isActive()) {
            addUserToChannel(channel, user);
            data.add(channel);
            channel.setActive(true);
        }
        return channel;
    }

    // 현재 존재하는 모든 채널 확인
    @Override
    public List<Channel> getChannels() {
        return data;
    }


    // 채널의 ID를 사용하여 채널 검색
    @Override
    public Optional<Channel> getChannelById(UUID channelId) {
        return data.stream()
                .filter(ch -> ch.getId().equals(channelId))
                .findFirst();
    }

    // 채널 업데이트 ( 정보 수정 )
    // 현재는 수정 가능한 입력이 channelName 1개
    @Override
    public void updateChannel(UUID channelId, String channelName) {
        Optional<Channel> tmp = data.stream()
                .filter(ch -> ch.getId().equals(channelId))
                .findFirst();
        if (tmp.isPresent()) {
            tmp.get().setChannelName(channelName);
            tmp.get().setUpdatedAt(System.currentTimeMillis());
        }
    }

    // 채널 삭제
    @Override
    public void deleteChannel(Channel channel) {
        if (data.contains(channel)) {
            data.remove(channel);  // 전체 채널 리스트에서 해당 채널 삭제

            /***********************************
             * 채널을 가지고 있는 유저에게 채널 삭제
             ***********************************/
            channel.getUsers().stream()
                    .forEach(user -> {
                        user.getChannels().remove(channel);
                        user.setUpdatedAt(System.currentTimeMillis());
                    });
            // 모든 유저에게 해당 채널 삭제

            /***********************************
             * 전체 메시지 중 해당 채널의 메시지 삭제
             ***********************************/
            channel.getMessages().stream()
                    .forEach(message -> {
                        jcfMessageService.removeMessage(message);
                        message.setUpdatedAt(System.currentTimeMillis());
                    });// 채널 내 모든 메세지 삭제
        }
    }

    /***********************************
     * 해당 채널의 존재하는 특정 유저를 id를 통해 삭제
     * @param channel 삭제할 유저가 있는 채널
     * @param userId 삭제할 유저의 id
     ***********************************/
    @Override
    public void removeUserFromChannel(Channel channel, UUID userId) {
        Optional<User> target = channel.getUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
        target.ifPresentOrElse(
                user -> {
                    user.removeChannel(channel);  // 유저에서 해당 채널을 삭제하고
                    channel.setUpdatedAt(System.currentTimeMillis());  // 채널의 업데이트 시간 수정
                },
                () -> System.out.println("해당유저가 채널에 없습니다."));
    }

    /***********************************
     * 해당 채널의 새로운 유저 추가
     * @param channel 유저를 추가할 채널
     * @param user 채널에 추가할 유저
     ***********************************/
    @Override
    public void addUserToChannel(Channel channel, User user) {
        if (user.isActive()) {
            channel.addUser(user);  // 해당 유저에 채널을 추가하고
            channel.setUpdatedAt(System.currentTimeMillis());  // 채널의 업데이트 시간 수정
        }
    }
}
