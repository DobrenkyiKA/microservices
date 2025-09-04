package com.kdob.resourceservice.facade;

import com.kdob.resourceservice.dto.request.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.response.CreateResourceResponseDto;
import com.kdob.resourceservice.dto.response.DeletedResourcesResponseDto;
import com.kdob.resourceservice.mapper.ResourceMapper;
import com.kdob.resourceservice.pojo.Resource;
import com.kdob.resourceservice.service.ResourceService;
import com.kdob.resourceservice.service.SongMetadataIntegrationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ResourceFacade {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final SongMetadataIntegrationService songMetadataIntegrationService;

    public CreateResourceResponseDto createResource(final CreateResourceRequestDto createResourceRequestDto) {
        final Resource resourceToPersist = resourceMapper.dtoToPojo(createResourceRequestDto);
        final Resource persistedResource = resourceService.save(resourceToPersist);
        songMetadataIntegrationService.createSongMetadata(persistedResource);
        return resourceMapper.pojoToDto(persistedResource);
    }

    public byte[] getResource(final String id) {
        return resourceService.get(Long.parseLong(id));
    }

    public DeletedResourcesResponseDto deleteResource(final String id) {
        songMetadataIntegrationService.deleteSongMetadata(id);
        return resourceMapper.idsToDto(resourceService.delete(id));
    }
}

