package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService extends ErrorMessageService implements ChannelService {

    private static final ArrayList<Channel> channels = new ArrayList<Channel>();
    // Channel에 참여한 User 리스트 (주의사항: User에서 참여하고 있는 Channel 리스트와 동기화되어야 함) (**상호 동기화)
    // Channel 도 멤버를 알아야 하고,User도 Channel을 알아야 함. (메시지도 마찬가지)

    // Create
    @Override
    public Optional<Channel> addChannel(String name, User hostUser) {
        /*
            채널 생성 및 검사
            1. 호스트 유저가 null이 아니고, 상태가 ACTIVE여야 한다.
            2. 이름은 null이 될 수 없고, 공백이 될 수 없다.
            위의 조건을 어기면 Optional.empty();
         */

        if (hostUser != null && name != null && !name.trim().isEmpty() && hostUser.getStatus() == User.Status.ACTIVE) {
            Channel channel = new Channel(name, hostUser.getId());
            channels.add(channel);
            channel.setIsActive(true);
            channel.addUser(hostUser);
            hostUser.addChannel(channel);
            return Optional.of(channel);
        } else {
            printErrorMessage();
            return Optional.empty();
        }
    }

    // Read
    @Override
    public List<Channel> getChannels() {
        if (channels.isEmpty()) {
            System.out.println("<채널 리스트가 비어있습니다.>");
            return channels;
        } else {
            return channels;
        }
    }

    @Override
    public Optional<Channel> getChannelById(UUID id) {
        Optional<Channel> channel = channels.stream()
                .filter(c -> c.getId().equals(id))
                .filter(Channel::getIsActive)
                .findFirst();
        if (channel.isPresent()) {
            return channel;
        } else {
            printErrorMessage();
            return Optional.empty();
        }
    }

    // Update
    @Override
    public void updateChannelNameById(UUID channelId, UUID hostUserId, String name) {
        // 채널 유효성 검사
        Optional<Channel> c = getChannelById(channelId);
        Channel channel;
        if (c.isPresent()) {
            channel = c.get();
        } else {
            printErrorMessage();
            return;
        }

        if (channel.getIsActive()
                && channel.getHostUserId().equals(hostUserId)
                && name != null && !name.trim().isEmpty()) {
            channel.setChannelName(name);
            channel.setUpdatedAt(System.currentTimeMillis());
        } else {
            printErrorMessage();
        }
    }

    // Delete
    @Override
    public void deleteChannelById(UUID id, UUID hostUserId) {
        Optional<Channel> c = getChannelById(id);
        Channel channel;
        if (c.isPresent()) {
            channel = c.get();
        } else {
            printErrorMessage();
            return;
        }

        if (channel.getIsActive() && channel.getHostUserId().equals(hostUserId)) {
            // 채널을 삭제하면서, 채널에 남겨진 정보를 모두 삭제합니다
            channel.removeAllUsersAndMessages();
            channels.remove(channel);
            channel.setIsActive(false);
        } else {
            printErrorMessage();
        }
    }
}
