package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        uses = BinaryContentMapper.class
)
public interface UserMapper {


    @Mapping(target = "profile", ignore = true) // 비즈니스 로직에서 처리
    @Mapping(target = "role", ignore = true)
    User toEntity(UserCreateServiceRequest userCreateServiceRequest);

    @Mapping(target = "online", ignore = true)
    @Mapping(target = "profile", expression = "java(map(user.getOptionalProfile()))")
    UserResponse toResponse(User user);

    default BinaryContentResponse map(Optional<BinaryContent> binaryContent) {
        return binaryContent.map(BinaryContentResponse::new).orElse(null);
    }

}
