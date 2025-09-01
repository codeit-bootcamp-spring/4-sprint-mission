package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.dto.response.FileResponseDto;
import com.sprint.mission.discodeit.service.basic.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class AWSS3Test {
    private final S3UploadService s3;
    private final S3Presigner presigner;   // ✅ 프리사이너 주입
    private final AwsProperties props;     // 버킷/리전 접근

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
        String url = s3.store(file);
        return ResponseEntity.created(URI.create(url)).build();
    }

    /**
     * 파일 목록 JSON
     * 예: GET /files/list?prefix=images/2025/08&max=50
     */
    @GetMapping("/list")
    public List<FileResponseDto> list(
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "max", required = false, defaultValue = "100") int max
    ) {
        return s3.list(prefix, max);
    }

    /**
     * 강제 다운로드(브라우저 저장 대화상자 유도)
     * 예: GET /files/download?key=images/2025/08/abc.png
     *     GET /files/download?key=...&filename=내파일.png
     */
    @GetMapping("/download")
    public ResponseEntity<Void> download(
            @RequestParam("key") String key,
            @RequestParam(value = "filename", required = false) String filename
    ) {
        String bucket = props.getS3().getBucket();
        String name = (filename != null && !filename.isBlank())
                ? filename
                : Paths.get(key).getFileName().toString();

        // 응답 헤더(Content-Disposition)를 presign 시점에 주입
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + name + "\"")
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        String signed = presigner.presignGetObject(preReq).url().toString();
        return ResponseEntity.status(302).location(URI.create(signed)).build();
    }

}

