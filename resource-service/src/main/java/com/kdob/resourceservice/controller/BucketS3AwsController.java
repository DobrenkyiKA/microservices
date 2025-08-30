package com.kdob.resourceservice.controller;

import com.kdob.resourceservice.service.BucketS3AwsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buckets")
@RequiredArgsConstructor
@Validated
public class BucketS3AwsController {

    private final BucketS3AwsService bucketS3AwsService;

    @PostMapping(path = "/{bucketName}", produces = "application/json")
    public ResponseEntity<Void> createBucket(@PathVariable final String bucketName) {
        bucketS3AwsService.createBucket(bucketName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteBucket(@RequestParam final String bucketName) {
        bucketS3AwsService.deleteBucket(bucketName);
        return ResponseEntity.ok().build();
    }
}
