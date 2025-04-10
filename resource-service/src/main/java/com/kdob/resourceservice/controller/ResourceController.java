package com.kdob.resourceservice.controller;

import com.kdob.resourceservice.constraint.PositiveIntegerId;
import com.kdob.resourceservice.constraint.ValidCsvId;
import com.kdob.resourceservice.dto.request.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.response.CreateResourceResponseDto;
import com.kdob.resourceservice.dto.response.DeletedResourcesResponseDto;
import com.kdob.resourceservice.facade.ResourceFacade;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
public class ResourceController {

    private final ResourceFacade resourceFacade;

    @PostMapping(consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<CreateResourceResponseDto> createResource(@RequestBody final CreateResourceRequestDto createResourceRequestDto) {
        return ResponseEntity.ok(resourceFacade.createResource(createResourceRequestDto));
    }

    @GetMapping(produces = "audio/mpeg", path = "/{id}")
    public ResponseEntity<byte[]> getResource(@PathVariable @PositiveIntegerId final String id) {
        return ResponseEntity.ok(resourceFacade.getResource(id));
    }

    @DeleteMapping()
    public ResponseEntity<DeletedResourcesResponseDto> deleteResource(@RequestParam @ValidCsvId final String id) {
        return ResponseEntity.ok(resourceFacade.deleteResource(id));
    }
}
