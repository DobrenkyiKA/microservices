package com.kdob.storageservice.service;

import com.kdob.storageservice.model.Storage;
import com.kdob.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private static final String CREATING_STORAGE = "Creating storage with type: [{}], bucket: [{}], path: [{}]";

    private final StorageRepository storageRepository;

    public Storage createStorage(final Storage storage) {
        log.info(CREATING_STORAGE, storage.getStorageType(), storage.getBucket(), storage.getPath());
        return storageRepository.save(storage);
    }

    public List<Storage> getAllStorages() {
        return storageRepository.findAll();
    }

    public List<Long> deleteStorages(final String idsString) {
        final List<String> idStrings = Arrays.asList(idsString.split(","));
        return idStrings.stream()
                .map(Long::parseLong)
                .filter(deleteExistingStorages())
                .toList();
    }

    private Predicate<Long> deleteExistingStorages() {
        return id -> {
            if (storageRepository.existsById(id)) {
                storageRepository.deleteById(id);
                log.info("Deleted storage with id: [{}]", id);
                return true;
            } else {
                log.warn("Storage with id [{}] not found, skipping deletion", id);
                return false;
            }
        };
    }
}