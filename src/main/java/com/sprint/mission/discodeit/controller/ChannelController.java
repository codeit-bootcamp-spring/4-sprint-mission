package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/private",method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto channelDto = channelService.makeDto(channel);

        return new ResponseEntity<>(channelDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/public",method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto channelDto = channelService.makeDto(channel);
        return new ResponseEntity<>(channelDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{channel-id}",method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> patchPublicChannel(@PathVariable("channel-id") UUID channelId, @RequestBody PublicChannelUpdateRequest request) {
        Channel updatedChannel =  channelService.update(channelId, request);
        ChannelDto channelDto = channelService.makeDto(updatedChannel);
        return new ResponseEntity<>(channelDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{channel-id}",method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePublicChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/user/{user-id}",method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getPublicChannelByUserId(@PathVariable("user-id") UUID userId) {
        List<ChannelDto> foundChannel = channelService.findAllByUserId(userId);
        return new ResponseEntity<>(foundChannel, HttpStatus.OK);
    }

}
