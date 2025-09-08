package com.kdob.resourceservice.facade;

import com.kdob.resourceservice.dto.request.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.response.CreateResourceResponseDto;
import com.kdob.resourceservice.dto.response.DeletedResourcesResponseDto;
import com.kdob.resourceservice.dto.response.GetResourceResponseDto;
import com.kdob.resourceservice.integration.SongServiceIntegrationService;
import com.kdob.resourceservice.mapper.ResourceMapper;
import com.kdob.resourceservice.messaging.ResourceEventPublisher;
import com.kdob.resourceservice.pojo.Resource;
import com.kdob.resourceservice.service.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ResourceFacade {
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final ResourceEventPublisher resourceEventPublisher;
    private final SongServiceIntegrationService songServiceIntegrationService;

    public CreateResourceResponseDto createResource(final CreateResourceRequestDto createResourceRequestDto) {
        final Resource resourceToPersist = resourceMapper.dtoToPojo(createResourceRequestDto);
        final Resource persistedResource = resourceService.save(resourceToPersist);
        resourceEventPublisher.publishResourceCreated(persistedResource.getId());
        return resourceMapper.pojoToDto(persistedResource);
    }

    public GetResourceResponseDto getResource(final long id) {
        return new GetResourceResponseDto(resourceService.get(id), id);
    }

    public DeletedResourcesResponseDto deleteResource(final String id) {
        songServiceIntegrationService.deleteSongMetadata(id);
        return resourceMapper.idsToDto(resourceService.delete(id));
    }
}

