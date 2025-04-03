package com.kdob.resourceservice.controller;

import com.kdob.resourceservice.dto.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.CreateResourceResponseDto;
import com.kdob.resourceservice.dto.DeletedResourcesResponseDto;
import com.kdob.resourceservice.facade.ResourceFacade;
import jakarta.validation.constraints.Positive;
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
    public ResponseEntity<byte[]> getResource(@Positive(message = "Must be a positive integer") @PathVariable final String id) {
        return ResponseEntity.ok(resourceFacade.getResource(id));
    }

    @DeleteMapping()
    public ResponseEntity<DeletedResourcesResponseDto> deleteResource(@RequestParam @Size(max = 200, message = "maximum allowed is 200") final String id) {
        return ResponseEntity.ok(resourceFacade.deleteResource(id));
    }
}
