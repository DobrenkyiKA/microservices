package com.kdob.resourceservice.facade;

import com.kdob.resourceservice.dao.ResourceDao;
import com.kdob.resourceservice.dto.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.CreateResourceResponseDto;
import com.kdob.resourceservice.dto.DeletedResourcesResponseDto;
import com.kdob.resourceservice.service.SongMetadataIntegrationService;
import com.kdob.resourceservice.mapper.ResourceMapper;
import com.kdob.resourceservice.service.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ResourceFacade {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final SongMetadataIntegrationService songMetadataIntegrationService;


    public CreateResourceResponseDto createResource(final CreateResourceRequestDto createResourceRequestDto) {
        final ResourceDao resourceToCreate = resourceMapper.dtoToDao(createResourceRequestDto);
        final ResourceDao createdResource = resourceService.createResource(resourceToCreate);
        songMetadataIntegrationService.createSongMetadata(createdResource);
        return resourceMapper.daoToDto(createdResource);
    }
    public byte[] getResource(final String id) {
        return resourceService.getResource(id);
    }
    public DeletedResourcesResponseDto deleteResource(final String id) {
        return resourceService.deleteResource(id);
    }
}

