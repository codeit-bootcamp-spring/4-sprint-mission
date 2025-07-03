package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.CreateBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponseDto createBinaryContents(CreateBinaryContentRequestDto createBinaryContentRequestDto){
        BinaryContent newBinaryContent = new BinaryContent(createBinaryContentRequestDto.getReferenceId(),  createBinaryContentRequestDto.getFilePath(), createBinaryContentRequestDto.getBinaryContentType());
        binaryContentRepository.createBinaryContent(newBinaryContent);
        return new BinaryContentResponseDto(newBinaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllBinaryContentDtos(){
        List<BinaryContent> binaryContents = binaryContentRepository.loadBinaryContents();
        List<BinaryContentResponseDto> binaryContentResponseDtos = new ArrayList<>();

        for (BinaryContent binaryContent : binaryContents){
            binaryContentResponseDtos.add(new BinaryContentResponseDto(binaryContent));
        }
        return binaryContentResponseDtos;
    }

    @Override
    public List<BinaryContentResponseDto> findBinaryContentDtosByReferenceId(UUID referenceId){
        List<BinaryContentResponseDto> binaryContentResponseDtos = new ArrayList<>();
        List<BinaryContent> binaryContentList = binaryContentRepository.findBinaryContentListByReferenceId(referenceId);

        for (BinaryContent binaryContent : binaryContentList){
            binaryContentResponseDtos.add(new BinaryContentResponseDto(binaryContent));
        }
        return binaryContentResponseDtos;
    }

    @Override
    public BinaryContentResponseDto findBinaryContentDtoByBinaryContentId(UUID binaryContentId){
        Optional<BinaryContent> binaryContent = binaryContentRepository.findBinaryContentByBinaryContentId(binaryContentId);

        if(binaryContent.isEmpty()){
            throw new IllegalArgumentException("binaryContentId 정보가 없습니다.");
        }
        return new BinaryContentResponseDto(binaryContent.get());
    }

    @Override
    public BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId){
        Optional<BinaryContent> binaryContent = binaryContentRepository.findBinaryContentByBinaryContentId(binaryContentId);
        if(binaryContent.isEmpty()){
            throw new IllegalArgumentException("binaryContentId 정보가 없습니다.");
        }
        return binaryContent.get();
    }

    @Override
    public void deleteBinaryContentDTOById(UUID binaryContentId){
        binaryContentRepository.deleteBinaryContentByBinaryContentId(binaryContentId);
    }

}
