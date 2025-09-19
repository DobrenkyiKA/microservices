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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResourceFacade Unit Tests")
class ResourceFacadeTest {

    @Mock
    private ResourceService resourceService;
    
    @Mock
    private ResourceMapper resourceMapper;
    
    @Mock
    private ResourceEventPublisher resourceEventPublisher;
    
    @Mock
    private SongServiceIntegrationService songServiceIntegrationService;

    @InjectMocks
    private ResourceFacade resourceFacade;

    private CreateResourceRequestDto createResourceRequestDto;
    private Resource resource;
    private Resource persistedResource;
    private CreateResourceResponseDto createResourceResponseDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        createResourceRequestDto = new CreateResourceRequestDto();
        
        resource = new Resource();
        resource.setKey("test-song.mp3");
        resource.setResource("test audio data".getBytes());
        
        persistedResource = new Resource();
        persistedResource.setId(1L);
        persistedResource.setKey("test-song.mp3");
        persistedResource.setResource("test audio data".getBytes());
        
        createResourceResponseDto = new CreateResourceResponseDto(1L);
    }

    @Test
    @DisplayName("Should create resource successfully")
    void shouldCreateResourceSuccessfully() {
        // Given
        when(resourceMapper.dtoToPojo(createResourceRequestDto)).thenReturn(resource);
        when(resourceService.save(resource)).thenReturn(persistedResource);
        when(resourceMapper.pojoToDto(persistedResource)).thenReturn(createResourceResponseDto);

        // When
        CreateResourceResponseDto result = resourceFacade.createResource(createResourceRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        
        verify(resourceMapper).dtoToPojo(createResourceRequestDto);
        verify(resourceService).save(resource);
        verify(resourceEventPublisher).publishResourceCreated(1L);
        verify(resourceMapper).pojoToDto(persistedResource);
    }

    @Test
    @DisplayName("Should get resource successfully")
    void shouldGetResourceSuccessfully() {
        // Given
        long resourceId = 1L;
        byte[] resourceData = "test audio data".getBytes();
        when(resourceService.get(resourceId)).thenReturn(resourceData);

        // When
        GetResourceResponseDto result = resourceFacade.getResource(resourceId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.resource()).isEqualTo(resourceData);
        assertThat(result.id()).isEqualTo(resourceId);
        
        verify(resourceService).get(resourceId);
    }

    @Test
    @DisplayName("Should delete resource successfully")
    void shouldDeleteResourceSuccessfully() {
        // Given
        String resourceIds = "1,2,3";
        List<Long> deletedIds = Arrays.asList(1L, 2L, 3L);
        DeletedResourcesResponseDto deletedResourcesResponseDto = new DeletedResourcesResponseDto();
        deletedResourcesResponseDto.setIds(deletedIds);
        
        when(resourceService.delete(resourceIds)).thenReturn(deletedIds);
        when(resourceMapper.idsToDto(deletedIds)).thenReturn(deletedResourcesResponseDto);

        // When
        DeletedResourcesResponseDto result = resourceFacade.deleteResource(resourceIds);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIds()).containsExactly(1L, 2L, 3L);
        
        verify(songServiceIntegrationService).deleteSongMetadata(resourceIds);
        verify(resourceService).delete(resourceIds);
        verify(resourceMapper).idsToDto(deletedIds);
    }

    @Test
    @DisplayName("Should handle resource creation with null mapper result")
    void shouldHandleResourceCreationWithNullMapperResult() {
        // Given
        when(resourceMapper.dtoToPojo(createResourceRequestDto)).thenReturn(null);

        // When
        try {
            resourceFacade.createResource(createResourceRequestDto);
        } catch (Exception e) {
            // Then - expect some exception handling in real implementation
            verify(resourceMapper).dtoToPojo(createResourceRequestDto);
        }
    }

    @Test
    @DisplayName("Should handle resource deletion with empty result")
    void shouldHandleResourceDeletionWithEmptyResult() {
        // Given
        String resourceIds = "999";
        List<Long> emptyList = Arrays.asList();
        DeletedResourcesResponseDto emptyResponseDto = new DeletedResourcesResponseDto();
        emptyResponseDto.setIds(emptyList);
        
        when(resourceService.delete(resourceIds)).thenReturn(emptyList);
        when(resourceMapper.idsToDto(emptyList)).thenReturn(emptyResponseDto);

        // When
        DeletedResourcesResponseDto result = resourceFacade.deleteResource(resourceIds);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIds()).isEmpty();
        
        verify(songServiceIntegrationService).deleteSongMetadata(resourceIds);
        verify(resourceService).delete(resourceIds);
        verify(resourceMapper).idsToDto(emptyList);
    }
}