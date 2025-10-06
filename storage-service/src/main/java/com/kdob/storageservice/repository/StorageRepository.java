package com.kdob.storageservice.repository;

import com.kdob.storageservice.model.Storage;
import com.kdob.storageservice.model.StorageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    
    Optional<Storage> findByStorageType(StorageType storageType);
}