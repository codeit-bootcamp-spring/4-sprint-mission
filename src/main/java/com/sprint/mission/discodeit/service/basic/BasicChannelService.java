package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor // @NotNull 이거나 final인 필드를 초기화시켜주는 생성자 자동 생성
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private void updateChannel(Channel channel , String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(channel.getName())) {
            channel.setName(newName);
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(channel.getDescription())) {
            channel.setDescription(newDescription);
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            channel.setUpdatedAt(Instant.now());
        }
    }


    @Override
    public  ChannelCreateResponse createPublicChannel(PublicChannelRequest publicChannelRequest) {
        Channel channel = new Channel(publicChannelRequest);
        channelRepository.save(channel);
        return new ChannelCreateResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }

    @Override
    public ChannelCreateResponse createPrivateChannel(PrivateChannelRequsest privateChannelRequsest) {
        Channel channel = new Channel(privateChannelRequsest);

        for(UUID userId : privateChannelRequsest.participantUserIds()) {
            ReadStatus readStatus = new ReadStatus(userId,channel.getId(), Instant.now());
            readStatusRepository.save(readStatus);
        }
        channelRepository.save(channel);
        return new ChannelCreateResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }

    @Override
    public ChannelDetailResponse find(UUID channelId) {
        Channel channel = channelRepository
                .findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        Instant latestMessageAt = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = null;
        if(channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAll().stream()
                    .filter(rs->rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .distinct()
                    .toList();
        }

        return new ChannelDetailResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                latestMessageAt,
                participantIds
        );

    }

    @Override
    public List<ChannelDetailResponse> findAll() {
        return channelRepository.findAll().stream()
                .map(channel -> {
                    Instant latestMessageAt = messageRepository.findAll().stream()
                            .filter(m -> m.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

                    List<UUID> participants = channel.getType() == ChannelType.PRIVATE
                            ? readStatusRepository.findByChannelId(channel.getId()).stream()
                            .map(ReadStatus::getUserId)
                            .distinct()
                            .toList()
                            : null;

                    return new ChannelDetailResponse(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            channel.getCreatedAt(),
                            channel.getUpdatedAt(),
                            latestMessageAt,
                            participants
                    );
                })
                .toList();
    }

    @Override
    public List<ChannelDetailResponse> findAllByUserId(UUID userId) {
        List<UUID> joinedPrivateChannelIds = readStatusRepository.findByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .distinct()
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC ||
                                (channel.getType() == ChannelType.PRIVATE && joinedPrivateChannelIds.contains(channel.getId())))
                .map(channel -> {
                    Instant latestMessageAt = messageRepository.findAll().stream()
                            .filter(m -> m.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

                    List<UUID> participants = channel.getType() == ChannelType.PRIVATE
                            ? readStatusRepository.findByChannelId(channel.getId()).stream()
                            .map(ReadStatus::getUserId)
                            .distinct()
                            .toList()
                            : null;

                    return new ChannelDetailResponse(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            channel.getCreatedAt(),
                            channel.getUpdatedAt(),
                            latestMessageAt,
                            participants
                    );
                })
                .toList();
    }

    @Override
    public ChannelCreateResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

        if(channel.getType() == ChannelType.PUBLIC) {
            updateChannel(channel,request.newName(), request.newDescription());
            channelRepository.save(channel);
            return new ChannelCreateResponse(
                    channel.getId(),
                    channel.getCreatedAt(),
                    channel.getUpdatedAt(),
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription()
            );
        }
        else {
            throw new UnsupportedOperationException("PRIVATE 채널은 수정할 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.findByChannelId(channel.getId()).forEach(message ->
                messageRepository.deleteById(message.getId())
        );

        readStatusRepository.deleteByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

}
