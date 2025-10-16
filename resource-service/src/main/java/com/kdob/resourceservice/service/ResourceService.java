package com.kdob.resourceservice.service;

import com.kdob.resourceservice.pojo.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ResourceService {

    private final ResourceS3AwsService resourceS3AwsService;

    public Resource save(final Resource resource) {
        log.info("Saving resource with id=[{}]", resource.getId());
        return resourceS3AwsService.upload(resource);
    }

    public byte[] get(final long id) {
        log.info("Getting resource with id=[{}]", id);
        return resourceS3AwsService.download(id);
    }

    public List<Long> delete(final String id) {
        log.info("Deleting resources with ids: [{}]", id);
        final List<String> ids = Arrays.asList(id.split(","));
        final List<Long> longIds = ids.stream()
                .map(Long::parseLong)
                .toList();

        final List<Long> result = new ArrayList<>();
        for (Long lIds : longIds) {
            if (resourceS3AwsService.isExist(lIds)) {
                resourceS3AwsService.delete(lIds);
                log.info("Deleted resource with id: [{}]", lIds);
                result.add(lIds);
            }
        }
        return result;
    }
}
