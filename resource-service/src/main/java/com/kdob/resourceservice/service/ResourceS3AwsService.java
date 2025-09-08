package com.kdob.resourceservice.service;

import com.kdob.resourceservice.dao.ResourceInfoDao;
import com.kdob.resourceservice.exception.NoSuchResourceException;
import com.kdob.resourceservice.mapper.ResourceMapper;
import com.kdob.resourceservice.pojo.Resource;
import com.kdob.resourceservice.repository.ResourceInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static software.amazon.awssdk.regions.Region.ME_CENTRAL_1;

@Service
public class ResourceS3AwsService {

    private final ResourceInfoRepository resourceInfoRepository;
    private final ResourceMapper resourceMapper;
    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    private final S3Client s3Client;

    public ResourceS3AwsService(final ResourceInfoRepository resourceInfoRepository,
                                final ResourceMapper resourceMapper,
                                final S3Client s3Client) {
        this.resourceInfoRepository = resourceInfoRepository;
        this.resourceMapper = resourceMapper;
        this.s3Client = s3Client;
    }

    public Resource upload(final Resource resource) {
        resource.setKey(UUID.randomUUID().toString());
        s3Client.putObject(request ->
                        request
                                .bucket(BUCKET_NAME)
                                .key(resource.getKey()),
                RequestBody.fromBytes(resource.getResource()));
        final ResourceInfoDao resourceInfoToPersist = resourceMapper.daoToDto(resource);
        final ResourceInfoDao persistedResourceInfo = resourceInfoRepository.save(resourceInfoToPersist);
        resource.setId(persistedResourceInfo.getId());
        return resource;
    }

    public byte[] download(final long id) {
        final Optional<ResourceInfoDao> possibleResourceInfo = resourceInfoRepository.findById(id);
        ResourceInfoDao resourceInfoDao = possibleResourceInfo.orElseThrow(() -> new NoSuchResourceException("Resource with ID=" + id + " not found"));
        return getBytes(resourceInfoDao.getKey());
    }

    private byte[] getBytes(String key) {
        final ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(request ->
                request
                        .bucket(BUCKET_NAME)
                        .key(key));
        return object.asByteArray();
    }

    public boolean isExist(final long key) {
        return resourceInfoRepository.findById(key).isPresent();
    }

    public void delete(final long id) {
        final Optional<ResourceInfoDao> possibleResourceInfo = resourceInfoRepository.findById(id);
        ResourceInfoDao resourceInfoDao = possibleResourceInfo.orElseThrow(() -> new NoSuchResourceException("Resource with ID=" + id + " not found"));
        resourceInfoRepository.delete(resourceInfoDao);
        s3Client.deleteObject(request ->
                request
                        .bucket(BUCKET_NAME)
                        .key(resourceInfoDao.getKey()));
    }
// The following code just for testing purposes
    public void delete(final List<String> keys) {
        List<ObjectIdentifier> objectsToDelete = keys
                .stream()
                .map(key -> ObjectIdentifier
                        .builder()
                        .key(key)
                        .build())
                .toList();

        s3Client.deleteObjects(request ->
                request
                        .bucket(BUCKET_NAME)
                        .delete(deleteRequest ->
                                deleteRequest
                                        .objects(objectsToDelete)));
    }

    public void listAllObjectsInBucket() {
        S3Client s3Client = S3Client.builder()
                .region(ME_CENTRAL_1)
                .build();
        String nextContinuationToken = null;
        long totalObjects = 0;

        do {
            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .continuationToken(nextContinuationToken);

            ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());
            nextContinuationToken = response.nextContinuationToken();

            totalObjects += response.contents().stream()
                    .peek(System.out::println)
                    .reduce(0, (subtotal, element) -> subtotal + 1, Integer::sum);
        } while (nextContinuationToken != null);
        System.out.println("Number of objects in the bucket: " + totalObjects);

        s3Client.close();
    }

    public void listAllObjectsInBucketPaginated(int pageSize) {
        S3Client s3Client = S3Client.builder()
                .region(ME_CENTRAL_1)
                .build();

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .maxKeys(pageSize) // Set the maxKeys parameter to control the page size
                .build();

        ListObjectsV2Iterable listObjectsV2Iterable = s3Client.listObjectsV2Paginator(listObjectsV2Request);
        long totalObjects = 0;

        for (ListObjectsV2Response page : listObjectsV2Iterable) {
            long retrievedPageSize = page.contents().stream()
                    .peek(System.out::println)
                    .reduce(0, (subtotal, element) -> subtotal + 1, Integer::sum);
            totalObjects += retrievedPageSize;
            System.out.println("Page size: " + retrievedPageSize);
        }
        System.out.println("Total objects in the bucket: " + totalObjects);

        s3Client.close();
    }

    public void listAllObjectsInBucketPaginatedWithPrefix(int pageSize, String prefix) {
        S3Client s3Client = S3Client.builder()
                .region(ME_CENTRAL_1)
                .build();
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .maxKeys(pageSize) // Set the maxKeys parameter to control the page size
                .prefix(prefix) // Set the prefix
                .build();

        ListObjectsV2Iterable listObjectsV2Iterable = s3Client.listObjectsV2Paginator(listObjectsV2Request);
        long totalObjects = 0;

        for (ListObjectsV2Response page : listObjectsV2Iterable) {
            long retrievedPageSize = page.contents().stream().count();
            totalObjects += retrievedPageSize;
            System.out.println("Page size: " + retrievedPageSize);
        }
        System.out.println("Total objects in the bucket: " + totalObjects);

        s3Client.close();
    }
}
