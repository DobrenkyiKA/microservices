package com.kdob.resourceservice.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BucketS3AwsService {
    private final S3Client s3Client;

    public BucketS3AwsService(final S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void deleteAllBuckets() {
        getAllBuckets().forEach(bucket -> deleteBucket(bucket.name()));
    }

    private List<Bucket> getAllBuckets() {
        final List<Bucket> allBuckets = new ArrayList<>();
        String nextToken = null;

        do {
            final String continuationToken = nextToken;
            final ListBucketsResponse listBucketsResponse = s3Client.listBuckets(
                    request -> request.continuationToken(continuationToken)
            );

            allBuckets.addAll(listBucketsResponse.buckets());
            nextToken = listBucketsResponse.continuationToken();
        } while (nextToken != null);

        return allBuckets;
    }

    public void deleteBucket(final String bucketName) {
        if (isExist(bucketName)) {
            try {
                s3Client.deleteBucket(request -> request.bucket(bucketName));
            } catch (AwsServiceException | SdkClientException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createBucket(final String bucketName) {
        if (!isExist(bucketName)) {
            s3Client.createBucket(request -> request.bucket(bucketName));
        }
    }

    private boolean isExist(final String bucketName) {
        try {
            s3Client.headBucket(request -> request.bucket(bucketName));
            return true;
        } catch (NoSuchBucketException exception) {
            return false;
        }
    }
}
