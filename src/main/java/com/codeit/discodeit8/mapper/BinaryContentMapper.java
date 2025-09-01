package com.codeit.discodeit8.mapper;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit8.entity.BinaryContent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "fileName", source = "fileName")
  @Mapping(target = "size", source = "size")
  @Mapping(target = "contentType", source = "contentType")
  BinaryContentDto toBinaryContentDto(BinaryContent binaryContent);

  static BinaryContent attachmentToBinaryContent(MultipartFile attachment) throws IOException {

    if (attachment == null || attachment.isEmpty()) {
      BinaryContent binaryContent = new BinaryContent();

      File file = getBasicProfileFile();

      if (!file.exists()) {
        throw new IOException("기본 프로필 이미지가 존재하지 않습니다: " + file.getAbsolutePath());
      }

      String contentType = Files.probeContentType(file.toPath());
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      binaryContent.setFileName(file.getName());
      binaryContent.setSize(file.length());
      binaryContent.setContentType(contentType);
      return binaryContent;
    }

    BinaryContent binaryContent = new BinaryContent();

    binaryContent.setSize(attachment.getSize());
    binaryContent.setContentType(attachment.getContentType());
    binaryContent.setFileName(attachment.getOriginalFilename());

    return binaryContent;
  }

  static byte[] getBasicProfileBytes() throws IOException {
    File file = getBasicProfileFile();
    return Files.readAllBytes(file.toPath());
  }

  static File getBasicProfileFile() throws IOException {
    File file = new File("src/main/resources/profileImg/basicUserProfileImage.png");
    if (!file.exists()) {
      throw new IOException("기본 프로필 이미지가 존재하지 않습니다: " + file.getAbsolutePath());
    }
    return file;
  }
}
