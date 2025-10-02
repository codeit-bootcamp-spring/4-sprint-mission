package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    private final UserRepository userRepository;

    private final MessageRepository messageRepository;

    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelResponse createPublicChannel(ChannelCreateServiceRequest request) {
        log.info("public channel to create - name : {}, description : {}", request.getName(), request.getDescription());
        Channel channel = channelMapper.toEntity(request, ChannelType.PUBLIC);
        channelRepository.save(channel);
        log.info("public channel created successfully - id : {}", channel.getId());
        return channelMapper.toResponse(channel);
    }

    @Override
    @Transactional
    public ChannelResponse createPrivateChannel(PrivateChannelCreateServiceRequest request) {
        Channel channel = channelMapper.toEntity(request, ChannelType.PRIVATE);
        channelRepository.save(channel);

        List<ReadStatus> readStatuses = request.getParticipantIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)))
                .map(user -> new ReadStatus(user, channel))
                .toList();

        readStatusRepository.saveAll(readStatuses);

        log.info("private channel created successfully - id : {}", channel.getId());
        return channelMapper.toResponse(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse findChannelById(UUID channelId) {

        return channelRepository.findById(channelId)
                .map(channelMapper::toResponse)
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public  List<ChannelResponse> findAllChannelsByUserId(UUID userId) {
        List<UUID> joinChannels = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll()
                .stream()
                .filter(channel -> joinChannels.contains(channel.getId())|| channel.getType() == ChannelType.PUBLIC)
                .map(channelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChannelResponse updateChannel(ChannelUpdateServiceRequest request) {
        Channel channelToUpdate = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

        if(channelToUpdate.getType() == ChannelType.PRIVATE) {
            throw new ChannelException(ChannelErrorCode.PRIVATE_CHANNEL_NOT_EDITABLE);
        }

        log.info("channel to update - name : {}, description : {}", request.getName(), request.getDescription());
        Optional.ofNullable(request.getName()).ifPresent(channelToUpdate::editChannelName);
        Optional.ofNullable(request.getDescription()).ifPresent(channelToUpdate::editDescription);

        channelRepository.save(channelToUpdate);
        log.info("channel updated - name : {}, description : {}", channelToUpdate.getName(), channelToUpdate.getDescription());
        return channelMapper.toResponse(channelToUpdate);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
        log.info("deleted channel - id : {}", channelId);
    }
}

