package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {BinaryContentMapper.class})
public interface UserMapper {
  @Mapping(target = "online", ignore = true) // 서비스에서 직접 설정
  UserDto toDto(User user);
}
