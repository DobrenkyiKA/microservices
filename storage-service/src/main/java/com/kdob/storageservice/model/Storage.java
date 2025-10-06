package com.kdob.storageservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "storage")
public class Storage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "storage_seq")
    @SequenceGenerator(name = "storage_seq", sequenceName = "storage_id_seq", allocationSize = 1)
    private Long id;
    
    @NotNull(message = "Storage type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;
    
    @NotBlank(message = "Bucket is required")
    @Column(nullable = false)
    private String bucket;
    
    @NotBlank(message = "Path is required") 
    @Column(nullable = false)
    private String path;
}