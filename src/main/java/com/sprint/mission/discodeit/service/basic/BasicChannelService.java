package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel create(ChannelDto.createPublicChannel request) {
        Channel channel = request.toPublicChannel();
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(ChannelDto.createPrivateChannel request) {
        // UserRepository에 존재 하는 user인지 검사
        isUserExist(request.getParticipantIds());
        Channel channel = request.toPrivateChannel();
        Channel createdChannel = channelRepository.save(channel);
        request.getParticipantIds().stream()
                .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public ChannelDto.response find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channel -> channel.toDto(getParticipantIds(channelId), getLastMessageAt(channelId)))
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<ChannelDto.response> findAllByUserId(UUID userId) {
        isUserExist(userId);
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(channel -> channel.toDto(getParticipantIds(channel.getId()), getLastMessageAt(channel.getId())))
                .toList();
    }

    @Override
    public Channel update(UUID channelId, ChannelDto.updatePublicChannel request) {
        Optional<String> rawName = Optional.ofNullable(request.getNewName());
        Optional<String> rawDescription = Optional.ofNullable(request.getNewDescription());

        // 업데이트 할 값이 있는지 확인..
        if (rawName.isEmpty() && rawDescription.isEmpty()) {
            throw new IllegalArgumentException("Nothing to update");
        }

        // ChannelRepository에 저장된 값이 아니며 PRIVATE이 아니면 예외 발생..
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        String newName = channel.getName();
        String newDescription = channel.getDescription();

        if (rawName.isPresent()) {
            if (rawName.get().equals(channel.getName())) {
                throw new IllegalArgumentException("같은 이름으로 바꿀 수 없습니다.");
            }
            newName = rawName.get();
        }
        if (rawDescription.isPresent()) {
            if (rawDescription.get().equals(channel.getDescription())) {
                throw new IllegalArgumentException("바꿀 내용이 없습니다.");
            }
            newDescription = rawDescription.get();
        }

        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private List<UUID> getParticipantIds(UUID channelId) {
        List<UUID> participantIds = new ArrayList<>();
        readStatusRepository.findAllByChannelId(channelId)
                .stream()
                .map(ReadStatus::getUserId)
                .forEach(participantIds::add);
        return participantIds;
    }

    private Instant getLastMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);
    }

    private void isUserExist(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User " + userId + " not found");
        }
    }

    private void isUserExist(List<UUID> userIds) {
        for (UUID id : userIds) {
            if (!userRepository.existsById(id)) {
                throw new NoSuchElementException("User with id " + id + " not found");
            }
        }
    }
}
