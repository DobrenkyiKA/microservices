package com.kdob.resourceservice.service;

import com.kdob.resourceservice.dao.ResourceInfoDao;
import com.kdob.resourceservice.dto.StorageDto;
import com.kdob.resourceservice.exception.NoSuchResourceException;
import com.kdob.resourceservice.mapper.ResourceMapper;
import com.kdob.resourceservice.metrics.LoggingUtils;
import com.kdob.resourceservice.metrics.MetrixExampleService;
import com.kdob.resourceservice.pojo.Resource;
import com.kdob.resourceservice.repository.ResourceInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kdob.resourceservice.enumeration.StorageType.PERMANENT;
import static com.kdob.resourceservice.enumeration.StorageType.STAGING;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceS3AwsService {
    private final ResourceInfoRepository resourceInfoRepository;
    private final ResourceMapper resourceMapper;
    private final StorageService storageService;
    private final S3Client s3Client;
    private final MetrixExampleService metrixExampleService;

    public Resource upload(final Resource resource) {
        log.info("Uploading resource to STAGING storage");
        final Optional<StorageDto> stagingStorage = storageService.getStagingStorage();
        if (stagingStorage.isEmpty()) {
            throw new RuntimeException("STAGING storage not found");
        }

        final StorageDto storage = stagingStorage.get();
        resource.setKey(UUID.randomUUID().toString());

        s3Client.putObject(request ->
                        request
                                .bucket(storage.getBucket())
                                .key(storage.getPath() + "/" + resource.getKey()),
                RequestBody.fromBytes(resource.getResource()));

        final ResourceInfoDao resourceInfoToPersist = resourceMapper.daoToDto(resource);
        resourceInfoToPersist.setStorageType(STAGING);
        resourceInfoToPersist.setStorageId(storage.getId());

        final ResourceInfoDao persistedResourceInfo = resourceInfoRepository.save(resourceInfoToPersist);
        resource.setId(persistedResourceInfo.getId());

        log.info("Resource uploaded successfully to STAGING storage with ID: [{}]", resource.getId());
        metrixExampleService.useMetrics(resource);
        return resource;
    }

    public byte[] download(final long id) {
        final Optional<ResourceInfoDao> possibleResourceInfo = resourceInfoRepository.findById(id);
        ResourceInfoDao resourceInfoDao = possibleResourceInfo.orElseThrow(() -> new NoSuchResourceException("Resource with ID=" + id + " not found"));
        return getBytes(resourceInfoDao);
    }

    private byte[] getBytes(final ResourceInfoDao resourceInfo) {
        final Optional<StorageDto> storage = storageService.getStorageByType(resourceInfo.getStorageType());

        final String fullKey = storage.get().getPath() + "/" + resourceInfo.getKey();

        final ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(request ->
                request
                        .bucket(storage.get().getBucket())
                        .key(fullKey));
        return object.asByteArray();
    }

    public boolean isExist(final long key) {
        return resourceInfoRepository.findById(key).isPresent();
    }

    public void delete(final long id) {
        final Optional<ResourceInfoDao> possibleResourceInfo = resourceInfoRepository.findById(id);
        final ResourceInfoDao resourceInfoDao = possibleResourceInfo.orElseThrow(() -> new NoSuchResourceException("Resource with ID=" + id + " not found"));

        final Optional<StorageDto> storage = storageService.getStorageByType(resourceInfoDao.getStorageType());

        if (storage.isPresent()) {
            final StorageDto storageDto = storage.get();
            final String fullKey = storageDto.getPath() + "/" + resourceInfoDao.getKey();

            s3Client.deleteObject(request ->
                    request
                            .bucket(storageDto.getBucket())
                            .key(fullKey));
        }
        resourceInfoRepository.delete(resourceInfoDao);
    }

    public void moveToPermanentStorage(Long resourceId) {
        log.info("Moving resource [{}] from STAGING to PERMANENT storage", resourceId);

        final Optional<ResourceInfoDao> possibleResourceInfo = resourceInfoRepository.findById(resourceId);
        final ResourceInfoDao resourceInfo = possibleResourceInfo.orElseThrow(() ->
                new NoSuchResourceException("Resource with ID=" + resourceId + " not found"));

        if (resourceInfo.getStorageType() != STAGING) {
            log.warn("Resource [{}] is not in STAGING state, current state: [{}]", resourceId, resourceInfo.getStorageType());
            return;
        }

        final Optional<StorageDto> stagingStorage = storageService.getStagingStorage();
        final Optional<StorageDto> permanentStorage = storageService.getPermanentStorage();

        final StorageDto staging = stagingStorage.get();
        final StorageDto permanent = permanentStorage.get();

        final String stagingKey = staging.getPath() + "/" + resourceInfo.getKey();
        final String permanentKey = permanent.getPath() + "/" + resourceInfo.getKey();

        LoggingUtils.logWithContext(log, "Moving resource to permanent storage", Map.of(
                "resourceInfoId", resourceInfo.getId().toString(),
                "resourceInfoStorageId", resourceInfo.getStorageId().toString(),
                "resourceInfoKey", resourceInfo.getKey()
        ));
        try {
            s3Client.copyObject(request ->
                    request.sourceBucket(staging.getBucket())
                            .sourceKey(stagingKey)
                            .destinationBucket(permanent.getBucket())
                            .destinationKey(permanentKey));

            s3Client.deleteObject(request ->
                    request.bucket(staging.getBucket())
                            .key(stagingKey));

            resourceInfo.setStorageType(PERMANENT);
            resourceInfo.setStorageId(permanent.getId());
            resourceInfoRepository.save(resourceInfo);
            log.info("Successfully moved resource [{}] to PERMANENT storage", resourceId);
        } catch (final Exception e) {
            LoggingUtils.logError(log, "Failed to move resource to PERMANENT storage", e, Map.of(
                    "resourceInfoId", resourceInfo.getId(),
                    "resourceInfoStorageId", resourceInfo.getStorageId(),
                    "resourceInfoKey", resourceInfo.getKey()
            ));
            log.error("Failed to move resource [{}] to PERMANENT storage: [{}]", resourceId, e.getMessage(), e);
            throw new RuntimeException("Failed to move resource to permanent storage", e);
        }
    }
}
