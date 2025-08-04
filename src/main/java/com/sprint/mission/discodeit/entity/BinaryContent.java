package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity implements Serializable {
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private String fileName;

    public BinaryContent(String contentType, Long size, String fileName) {
        super();
        this.contentType = contentType;
        this.size = size;
        this.fileName = fileName;
    }

    public BinaryContent(MultipartFile file) {
        super();
        this.contentType = file.getContentType();
        this.size = file.getSize();
        this.fileName = file.getOriginalFilename();
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", contentType=" + contentType +
                '}';
    }
}


