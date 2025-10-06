package com.kdob.storageservice.config;

import com.kdob.storageservice.model.Storage;
import com.kdob.storageservice.model.StorageType;
import com.kdob.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializationConfig implements CommandLineRunner {
    
    private final StorageRepository storageRepository;
    
    @Override
    public void run(String... args) {
        initializeStorageTypes();
    }
    
    private void initializeStorageTypes() {
        log.info("Initializing default storage types");
        
        // Check if STAGING storage already exists
        if (storageRepository.findByStorageType(StorageType.STAGING).isEmpty()) {
            Storage stagingStorage = new Storage();
            stagingStorage.setStorageType(StorageType.STAGING);
            stagingStorage.setBucket("staging-bucket");
            stagingStorage.setPath("/staging");
            
            storageRepository.save(stagingStorage);
            log.info("Created STAGING storage: bucket={}, path={}", 
                    stagingStorage.getBucket(), stagingStorage.getPath());
        } else {
            log.info("STAGING storage already exists, skipping creation");
        }
        
        // Check if PERMANENT storage already exists
        if (storageRepository.findByStorageType(StorageType.PERMANENT).isEmpty()) {
            Storage permanentStorage = new Storage();
            permanentStorage.setStorageType(StorageType.PERMANENT);
            permanentStorage.setBucket("permanent-bucket");
            permanentStorage.setPath("/permanent");
            
            storageRepository.save(permanentStorage);
            log.info("Created PERMANENT storage: bucket={}, path={}", 
                    permanentStorage.getBucket(), permanentStorage.getPath());
        } else {
            log.info("PERMANENT storage already exists, skipping creation");
        }
        
        log.info("Storage types initialization completed");
    }
}