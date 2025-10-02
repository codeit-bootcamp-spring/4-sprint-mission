package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Arrays;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-02T14:30:11+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class BinaryContentMapperImpl implements BinaryContentMapper {

    @Override
    public BinaryContentResponse toResponse(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        BinaryContentResponse.BinaryContentResponseBuilder binaryContentResponse = BinaryContentResponse.builder();

        binaryContentResponse.id( binaryContent.getId() );
        binaryContentResponse.fileName( binaryContent.getFileName() );
        binaryContentResponse.size( binaryContent.getSize() );
        binaryContentResponse.contentType( binaryContent.getContentType() );
        byte[] bytes = binaryContent.getBytes();
        if ( bytes != null ) {
            binaryContentResponse.bytes( Arrays.copyOf( bytes, bytes.length ) );
        }

        return binaryContentResponse.build();
    }
}
