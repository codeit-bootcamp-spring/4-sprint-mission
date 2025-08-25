package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  @Mapping(target = "channelId", source = "channel.id")
  @Mapping(target = "author", source = "author")
  @Mapping(target = "attachments", source = "attachments")
  MessageDto toMessageDto(Message message);


  @Mapping(target = "author", source = "user")
  @Mapping(target = "channel", source = "channel")
  @Mapping(target = "content", source = "content")
  @Mapping(target = "attachments", source = "attachments")
  Message toMessage(String content, User user, Channel channel, List<BinaryContent> attachments);

}