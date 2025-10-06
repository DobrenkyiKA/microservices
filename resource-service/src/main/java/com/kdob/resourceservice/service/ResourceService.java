package com.kdob.resourceservice.service;

import com.kdob.resourceservice.pojo.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceS3AwsService resourceS3AwsService;

    public Resource save(final Resource resource) {
        return resourceS3AwsService.upload(resource);
    }

    public byte[] get(final long id) {
        return resourceS3AwsService.download(id);
    }

    public List<Long> delete(final String id) {
        final List<String> ids = Arrays.asList(id.split(","));
        final List<Long> longIds = ids.stream()
                .map(Long::parseLong)
                .toList();

        final List<Long> result = new ArrayList<>();
        for (Long lIds : longIds) {
            if (resourceS3AwsService.isExist(lIds)) {
                resourceS3AwsService.delete(lIds);
                result.add(lIds);
            }
        }
        return result;
    }
}
