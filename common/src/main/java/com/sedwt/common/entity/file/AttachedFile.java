package com.sedwt.common.entity.file;

import com.sedwt.common.entity.BaseEntity;
import lombok.Data;

@Data
public class AttachedFile extends BaseEntity {
    private Long fileId;

    private String fileName;

    private String fileUrl;

    private Integer fileFormat;

    private Integer readTimes;

    private Integer downloadTimes;

    private Double fileSize;

    private Long projectId;

    private Integer fileType;

    private Integer status;
}
