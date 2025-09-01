package com.codeit.discodeit8.mapper;

import com.codeit.discodeit8.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import java.util.List;

public class PageResponseMapper {

  public static <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }

  public static <T> PageResponse<T> fromSlice(Slice<T> slice) {
    long totalElements = slice.hasNext()
        ? ((long) (slice.getNumber() + 1) * slice.getSize() + 1)
        : ((long) slice.getNumber() * slice.getSize() + slice.getNumberOfElements());

    return new PageResponse<>(
        slice.getContent(),
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        totalElements
    );
  }
}
// 현재 사용되지 않는 매퍼