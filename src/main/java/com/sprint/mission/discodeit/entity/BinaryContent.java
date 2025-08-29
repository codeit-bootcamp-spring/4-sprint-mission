package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.UUID;

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

    private BinaryContent(UUID id, MultipartFile file) {
        this(file);
        setId(id);
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", contentType=" + contentType +
                '}';
    }

    public static BinaryContent of(UUID id, MultipartFile file) {
        return new BinaryContent(id, file);
    }
}


