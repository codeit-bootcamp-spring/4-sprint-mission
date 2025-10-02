package com.sprint.mission.discodeit.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PageResponse <T>{
    private int size;
    private boolean hasNext;
    private List<T> content;
    private Object nextCursor;
    private Long totalElements;
}
