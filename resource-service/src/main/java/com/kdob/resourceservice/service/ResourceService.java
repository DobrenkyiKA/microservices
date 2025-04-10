package com.kdob.resourceservice.service;

import com.kdob.resourceservice.dao.ResourceDao;
import com.kdob.resourceservice.dto.response.DeletedResourcesResponseDto;
import com.kdob.resourceservice.exception.NoSuchResourceException;
import com.kdob.resourceservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final SongMetadataIntegrationService songMetadataIntegrationService;

    public ResourceDao createResource(final ResourceDao resource) {
        return resourceRepository.save(resource);
    }

    public byte[] getResource(final long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new NoSuchResourceException("Resource with ID=" + id + " not found")).getResource();
    }

    public DeletedResourcesResponseDto deleteResource(final String id) {
        songMetadataIntegrationService.deleteSongMetadata(id);

        List<String> ids = Arrays.asList(id.split(","));
        List<Long> array = ids.stream()
                .map(Long::parseLong)
                .toList();

        List<Long> result = new ArrayList<>();
        for (Long aLong : array) {
            if (resourceRepository.existsById(aLong)) {
                resourceRepository.deleteById(aLong);
                result.add(aLong);
            }

        }

        DeletedResourcesResponseDto deletedResourcesResponseDto = new DeletedResourcesResponseDto();
        deletedResourcesResponseDto.setIds(result);
        return deletedResourcesResponseDto;
    }

}
