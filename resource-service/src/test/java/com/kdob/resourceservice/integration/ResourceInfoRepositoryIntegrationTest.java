package com.kdob.resourceservice.integration;

import com.kdob.resourceservice.dao.ResourceInfoDao;
import com.kdob.resourceservice.repository.ResourceInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ResourceInfoRepository Integration Tests")
class ResourceInfoRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine3.21")
            .withDatabaseName("resource_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private ResourceInfoRepository resourceInfoRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        resourceInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save resource info successfully")
    void shouldSaveResourceInfoSuccessfully() {
        // Given
        ResourceInfoDao resourceInfo = new ResourceInfoDao();
        resourceInfo.setKey("test-audio-file.mp3");

        // When
        ResourceInfoDao savedResource = resourceInfoRepository.save(resourceInfo);

        // Then
        assertThat(savedResource).isNotNull();
        assertThat(savedResource.getId()).isNotNull();
        assertThat(savedResource.getKey()).isEqualTo("test-audio-file.mp3");
        
        System.out.println("[DEBUG_LOG] Saved resource with ID: " + savedResource.getId());
    }

    @Test
    @DisplayName("Should find resource info by id")
    void shouldFindResourceInfoById() {
        // Given
        ResourceInfoDao resourceInfo = new ResourceInfoDao();
        resourceInfo.setKey("another-test-file.mp3");
        ResourceInfoDao savedResource = resourceInfoRepository.save(resourceInfo);

        // When
        Optional<ResourceInfoDao> foundResource = resourceInfoRepository.findById(savedResource.getId());

        // Then
        assertThat(foundResource).isPresent();
        assertThat(foundResource.get().getKey()).isEqualTo("another-test-file.mp3");
        assertThat(foundResource.get().getId()).isEqualTo(savedResource.getId());
        
        System.out.println("[DEBUG_LOG] Found resource: " + foundResource.get().getKey());
    }

    @Test
    @DisplayName("Should return empty when resource info not found")
    void shouldReturnEmptyWhenResourceInfoNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<ResourceInfoDao> foundResource = resourceInfoRepository.findById(nonExistentId);

        // Then
        assertThat(foundResource).isEmpty();
        
        System.out.println("[DEBUG_LOG] Resource not found for ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should delete resource info successfully")
    void shouldDeleteResourceInfoSuccessfully() {
        // Given
        ResourceInfoDao resourceInfo = new ResourceInfoDao();
        resourceInfo.setKey("file-to-delete.mp3");
        ResourceInfoDao savedResource = resourceInfoRepository.save(resourceInfo);
        Long resourceId = savedResource.getId();

        // When
        resourceInfoRepository.deleteById(resourceId);

        // Then
        Optional<ResourceInfoDao> deletedResource = resourceInfoRepository.findById(resourceId);
        assertThat(deletedResource).isEmpty();
        
        System.out.println("[DEBUG_LOG] Successfully deleted resource with ID: " + resourceId);
    }

    @Test
    @DisplayName("Should count resources correctly")
    void shouldCountResourcesCorrectly() {
        // Given
        ResourceInfoDao resource1 = new ResourceInfoDao();
        resource1.setKey("file1.mp3");
        resourceInfoRepository.save(resource1);

        ResourceInfoDao resource2 = new ResourceInfoDao();
        resource2.setKey("file2.mp3");
        resourceInfoRepository.save(resource2);

        // When
        long count = resourceInfoRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
        
        System.out.println("[DEBUG_LOG] Total resource count: " + count);
    }

    @Test
    @DisplayName("Should handle multiple save operations")
    void shouldHandleMultipleSaveOperations() {
        // Given & When
        for (int i = 1; i <= 5; i++) {
            ResourceInfoDao resource = new ResourceInfoDao();
            resource.setKey("batch-file-" + i + ".mp3");
            resourceInfoRepository.save(resource);
        }

        // Then
        long totalCount = resourceInfoRepository.count();
        assertThat(totalCount).isEqualTo(5);
        
        Iterable<ResourceInfoDao> allResources = resourceInfoRepository.findAll();
        assertThat(allResources).hasSize(5);
        
        System.out.println("[DEBUG_LOG] Successfully saved 5 resources in batch operation");
    }
}