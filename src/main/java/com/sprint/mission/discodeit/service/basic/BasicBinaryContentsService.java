package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.CreateBinaryContentsRequestDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.FindBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.service.BinaryContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentsService implements BinaryContentsService {

    private final BinaryContentsRepository binaryContentsRepository;

    @Override
    public BinaryContentsResponseDto createBinaryContents(CreateBinaryContentsRequestDto createBinaryContentsRequestDto){
        BinaryContents newBinaryContents = new BinaryContents(createBinaryContentsRequestDto.getReferenceId(),  createBinaryContentsRequestDto.getFilePath(), createBinaryContentsRequestDto.getBinaryContentType());
        binaryContentsRepository.createBinaryContents(newBinaryContents);
        return new BinaryContentsResponseDto(newBinaryContents);
    }


    @Override
    public List<BinaryContentsResponseDto> findAllBinaryContentsDtos(){
        List<BinaryContents> binaryContents = binaryContentsRepository.loadBinaryContents();
        List<BinaryContentsResponseDto> binaryContentsResponseDtos = new ArrayList<>();

        for (BinaryContents binaryContent : binaryContents){
            binaryContentsResponseDtos.add(new BinaryContentsResponseDto(binaryContent));
        }
        return binaryContentsResponseDtos;
    }

    @Override
    public List<BinaryContentsResponseDto> findBinaryContentsDtosByReferenceId(UUID referenceId){
        List<BinaryContentsResponseDto> binaryContentsResponseDtos = new ArrayList<>();
        List<BinaryContents> binaryContents = binaryContentsRepository.findBinaryContentsByReferenceId(referenceId);

        for (BinaryContents binaryContent : binaryContents){
            binaryContentsResponseDtos.add(new BinaryContentsResponseDto(binaryContent));
        }
        return binaryContentsResponseDtos;
    }

    @Override
    public BinaryContentsResponseDto findBinaryContentsDTOByBinaryContentsId(UUID binaryContentId){
        Optional<BinaryContents> binaryContents = binaryContentsRepository.findBinaryContentsByBinaryContentsId(binaryContentId);

        if(binaryContents.isEmpty()){
            throw new IllegalArgumentException("binaryContentId 정보가 없습니다.");
        }
        return new BinaryContentsResponseDto(binaryContents.get());
    }

    @Override
    public void deleteBinaryContentsDTOById(UUID binaryContentId){
        binaryContentsRepository.deleteBinaryContensByBinaryContentsId(binaryContentId);
    }

}
