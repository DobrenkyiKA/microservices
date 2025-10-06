package com.kdob.storageservice.dto;

import com.kdob.storageservice.model.StorageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StorageRequestDto {
    
    @NotNull(message = "Storage type is required")
    private StorageType storageType;
    
    @NotBlank(message = "Bucket is required")
    private String bucket;
    
    @NotBlank(message = "Path is required")
    private String path;
}