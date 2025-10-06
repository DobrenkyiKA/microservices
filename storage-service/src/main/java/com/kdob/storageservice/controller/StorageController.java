package com.kdob.storageservice.controller;

import com.kdob.storageservice.constraint.ValidCsvId;
import com.kdob.storageservice.dto.StorageRequestDto;
import com.kdob.storageservice.dto.StorageResponseDto;
import com.kdob.storageservice.mapper.StorageMapper;
import com.kdob.storageservice.model.Storage;
import com.kdob.storageservice.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
@Validated
public class StorageController {
    
    private final StorageService storageService;
    private final StorageMapper storageMapper;
    
    @PostMapping
    public ResponseEntity<Map<String, Long>> createStorage(@Valid @RequestBody final StorageRequestDto requestDto) {
        log.info("Creating storage: [{}]", requestDto);
        final Storage storage = storageMapper.toEntity(requestDto);
        final Storage createdStorage = storageService.createStorage(storage);
        return ResponseEntity.ok(Map.of("id", createdStorage.getId()));
    }
    
    @GetMapping
    public ResponseEntity<List<StorageResponseDto>> getAllStorages() {
        log.info("Getting all storages");
        final List<Storage> storages = storageService.getAllStorages();
        return ResponseEntity.ok(storageMapper.toResponseDtoList(storages));
    }
    
    @DeleteMapping
    public ResponseEntity<List<Long>> deleteStorages(@RequestParam("id") @ValidCsvId final String ids) {
        log.info("Deleting storages with ids: [{}]", ids);
        return ResponseEntity.ok(storageService.deleteStorages(ids));
    }
}