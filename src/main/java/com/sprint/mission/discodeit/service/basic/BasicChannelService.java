package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.UnauthorizedChannelAccessException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponseDto createPublicChannel(CreatePublicChannelRequestDto createPublicChannelRequestDTO) {
        Optional<Channel> duplicateChannel = channelRepository.findChannelByChannelName(createPublicChannelRequestDTO.getChannelName());
        optionalChannelIsPresent(duplicateChannel);

        Channel channel = new Channel(createPublicChannelRequestDTO.getHostUserId(),
                createPublicChannelRequestDTO.getChannelName(),
                createPublicChannelRequestDTO.getDescription());

        User hostUser = findUserByUserId(createPublicChannelRequestDTO.getHostUserId());

        channel.addUser(hostUser);
        hostUser.addChannel(channel);

        channelRepository.createChannel(channel);
        userRepository.updateUser(hostUser);

        //createOrUpdateReadStatus(hostUser, channel);

        //List<Message> messages = messageRepository.findMessagesByChannelId(channel.getId());
        //return new ChannelResponseDto(channel, messages.get(messages.size()-1).getId());
        return new ChannelResponseDto(channel);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(CreatePrivateChannelRequestDto createPrivateChannelRequestDto) {

        User hostUser = findUserByUserId(createPrivateChannelRequestDto.getHostUserId());
        User enterUser = findUserByUserName(createPrivateChannelRequestDto.getGuestName());

        List<Channel> channelsInHostUser = channelRepository.findChannelsByUserId(hostUser.getId());
        Optional<Channel> duplicateChannel = channelsInHostUser.stream().filter(ch -> ch.getUserIds().contains(enterUser.getId())).findFirst();
        optionalChannelIsPresent(duplicateChannel);

        Channel channel = new Channel(hostUser.getId());
        channel.addUser(hostUser);
        channel.addUser(enterUser);

        hostUser.addChannel(channel);
        enterUser.addChannel(channel);

        channelRepository.createChannel(channel);
        userRepository.updateUser(hostUser);
        userRepository.updateUser(enterUser);

        return new ChannelResponseDto(channel);
    }

    @Override
    public ChannelResponseDto updateChannelName(ChannelNameUpdateRequestDto channelNameUpdateRequestDto) {
        User user = findUserByUserId(channelNameUpdateRequestDto.getUserId());
        Channel channel = findChannelByChannelName(channelNameUpdateRequestDto.getChannelOldName());

        if (!user.equalsId(channel.getHostUserId())){
            throw new UnauthorizedChannelAccessException("해당 채널의 호스트가 아니라 권한이 없습니다.");
        }

        Optional<Channel> duplicateNewName = channelRepository.findChannelByChannelName(channelNameUpdateRequestDto.getChannelNewName());
        optionalChannelIsPresent(duplicateNewName);

        channel.setChannelName(channelNameUpdateRequestDto.getChannelNewName());
        channelRepository.updateChannel(channel);

        return new ChannelResponseDto(channel);
    }

    @Override
    public void deleteChannel(DeleteChannelRequestDto deleteChannelRequestDto) {

        User user = findUserByUserId(deleteChannelRequestDto.getUserId());
        Channel channel = findChannelByChannelName(deleteChannelRequestDto.getChannelName());

        if (user.getStatus() == UserActivationState.DEACTIVE){
            System.out.println("유저 상태가 비활성입니다.\n");
            return;
        }

        if (!channel.getHostUserId().equals(user.getId())) {
            System.out.println("채널을 삭제할 권한이 없습니다.");
            return;
        }

        // 채널에서 유저 제거 같은 비즈니스 로직 수행
        List<User> users = userRepository.loadUsers();
        for (User u : users) {
            u.removeChannel(channel);
            u.getMessageIds().removeIf(messageId -> channel.getMessageIds().contains(messageId));
        }

        readStatusRepository.deleteReadStatusByChannelId(channel.getId());
        messageRepository.deleteMessagesByChannelId(channel.getId());
        userRepository.saveUsers(users);
        channelRepository.deleteChannel(channel);
    }

    @Override
    public void addUserToChannel(AddUserToChannelRequestDto addUserToChannelRequestDTO) {

        User user = findUserByUserId(addUserToChannelRequestDTO.getUserResponseDto().getUserId());
        Channel channel = findChannelByChannelId(addUserToChannelRequestDTO.getChannelResponseDto().getChannelId());

        if (user.getStatus() == UserActivationState.DEACTIVE){
            System.out.println("유저 상태가 비활성입니다.");
            return;
        }

        user.addChannel(channel);
        userRepository.updateUser(user);

        channel.addUser(user);
        channelRepository.updateChannel(channel);
    }

    @Override
    public void leaveUserFromChannel(LeaveUserFromChannelRequestDto leaveUserFromChannelRequestDTO) {
        User user = findUserByUserId(leaveUserFromChannelRequestDTO.getUserResponseDto().getUserId());
        Channel channel = findChannelByChannelId(leaveUserFromChannelRequestDTO.getChannelResponseDto().getChannelId());

        user.removeChannel(channel);
        userRepository.updateUser(user);
        // 유저 찾기 -> 해당 채널 삭제 -> 유저 업데이트

        channel.removeUser(user);
        channelRepository.updateChannel(channel);
    }

    @Override
    public void updateHostUser(ChannelHostUserUpdateRequestDto channelHostUserUpdateRequestDTO) {

        User oldHostUser = findUserByUserId(channelHostUserUpdateRequestDTO.getOldHostUserResponseDto().getUserId());
        User newHostUser = findUserByUserId(channelHostUserUpdateRequestDTO.getNewHostUserResponseDto().getUserId());
        Channel channel = findChannelByChannelId(channelHostUserUpdateRequestDTO.getChannelResponseDto().getChannelId());

        if (!(channel.getUserIds().contains(newHostUser.getId()) && channel.getUserIds().contains(oldHostUser.getId()))) {
            System.out.println("유저들이 해당 채널에 없어 변경할 수 없습니다.");
            return;
        }

        channel.setHostUserId(newHostUser.getId());
        channelRepository.updateChannel(channel);
    }


    @Override
    public List<ChannelResponseDto> findPublicChannels() {
        List<ChannelResponseDto> publicChannelsDTO = new ArrayList<>();
        List<Channel> channels = channelRepository.loadChannels();
        channels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PUBLIC_CHANNEL))
                .forEach(channel -> {
                    //publicChannelsDTO.add(new ChannelResponseDto(channel, messageRepository.findMessagesByChannelId(channel.getId()).get(messageRepository.findMessagesByChannelId(channel.getId()).size() -1 ).getId()));
                    publicChannelsDTO.add(new ChannelResponseDto(channel));
                });


        return publicChannelsDTO;
    }

    @Override
    public List<ChannelResponseDto> findAllChannelByUserId(UUID userId) {
        List<ChannelResponseDto> channelsDto = new ArrayList<>();

        List<Channel> channels = channelRepository.findChannelsByUserId(userId);

        for (Channel channel : channels) {
            channelsDto.add(new ChannelResponseDto(channel));
        }

        return channelsDto;
    }

    @Override
    public List<ChannelResponseDto> findPrivateChannelsByUserId(UserResponseDto userResponseDto) {
        List<ChannelResponseDto> publicChannelsDTO = new ArrayList<>();
        List<Channel> channels = channelRepository.findChannelsByUserId(userResponseDto.getUserId());
        channels.stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PRIVATE_CHANNEL))
                //.forEach(channel -> publicChannelsDTO.add(new ChannelResponseDto(channel, messageRepository.findMessagesByChannelId(channel.getId()).get(messageRepository.findMessagesByChannelId(channel.getId()).size() -1 ).getId())));
                .forEach(channel -> publicChannelsDTO.add(new ChannelResponseDto(channel)));

        return publicChannelsDTO;
    }

    @Override
    public ChannelResponseDto findChannelDtoByChannelId(UUID chanelId){
        Channel channel = findChannelByChannelId(chanelId);
        //List<Message> messages = messageRepository.findMessagesByChannelId(channel.getId());
        //return new ChannelResponseDto(channel, messages.get(messages.size()-1).getId());
        return new ChannelResponseDto(channel);
    }

    @Override
    public ChannelResponseDto findChannelDtoByChannelName(String channelName){
        Channel channel = findChannelByChannelName(channelName);
        return new ChannelResponseDto(channel);
    }

    @Override
    public void enterChannel(UUID userId, UUID channelId) {
        User user = findUserByUserId(userId);
        Channel channel = findChannelByChannelId(channelId);
        if(!channel.getUserIds().contains(userId)){
            throw new IllegalArgumentException("유저가 해당 채널을 가입되어 있지 않습니다.");
        }
    }

    private void optionalChannelIsPresent(Optional<Channel> channel) {
        if (channel.isPresent()) {
            throw new IllegalArgumentException("Channel 정보가 중복됩니다.");
        }
    }

    private Channel findChannelByChannelId(UUID channelId) {
        return channelRepository.findChannelByChannelId(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다."));
    }

    private User findUserByUserId(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }

    private User findUserByUserName(String userName) {
        return userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }

    private Channel findChannelByChannelName(String channelName) {
        return channelRepository.findChannelByChannelName(channelName)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다."));
    }


}
