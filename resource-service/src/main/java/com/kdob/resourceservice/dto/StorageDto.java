package com.kdob.resourceservice.dto;

import com.kdob.resourceservice.enumeration.StorageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageDto {
    private Long id;
    private StorageType storageType;
    private String bucket;
    private String path;
}