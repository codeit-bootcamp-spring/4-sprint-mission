package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @PostMapping( "/channels/public")
    public ResponseEntity<ChannelResponse> createPublicChannel(@Valid @RequestBody ChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublicChannel(request.toServiceRequest()));
    }

    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @PostMapping("/channels/private")
    public ResponseEntity<ChannelResponse> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPrivateChannel(request.toServiceRequest()));
    }

    @Override
    @GetMapping( "/channels/{channelId}")
    public ResponseEntity<ChannelResponse> findChannelById(@PathVariable("channelId") UUID channelId) {
        return ResponseEntity.ok().body(channelService.findChannelById(channelId));
    }

    @Override
    @GetMapping( "/channels")
    public ResponseEntity<List<ChannelResponse>> findAllChannelsByUserId(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok().body(channelService.findAllChannelsByUserId(userId));
    }

    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @PatchMapping("/channels/{channelId}")
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable("channelId") UUID channelId, @RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.ok().body(channelService.updateChannel(request.toServiceRequest(channelId)));
    }

    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @DeleteMapping( "/channels/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable("channelId") UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
}
