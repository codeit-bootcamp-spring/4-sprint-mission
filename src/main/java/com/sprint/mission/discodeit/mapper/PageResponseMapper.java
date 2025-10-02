package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

@Mapper
public interface PageResponseMapper {

    default <T> PageResponse<T> toPageResponse(Slice<T> slice, Object nextCursor) {
        return new PageResponse<>(
                slice.getSize(),
                slice.hasNext(),
                slice.getContent(),
                nextCursor,
                null
        );
    }
}
