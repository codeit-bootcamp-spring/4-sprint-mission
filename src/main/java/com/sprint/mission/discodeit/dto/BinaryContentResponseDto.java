package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinaryContentResponseDto {
    private UUID id;
    private String contentType;
    private byte[] content;

    @Override
    public String toString() {
        return "BinaryContentResponseDto{" +
                "id=" + id +
                ", contentType='" + contentType + '\'';
    }
}
