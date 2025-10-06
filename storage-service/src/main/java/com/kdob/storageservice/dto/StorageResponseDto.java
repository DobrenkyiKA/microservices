package com.kdob.storageservice.dto;

import com.kdob.storageservice.model.StorageType;
import lombok.Data;

@Data
public class StorageResponseDto {
    
    private Long id;
    private StorageType storageType;
    private String bucket;
    private String path;
}