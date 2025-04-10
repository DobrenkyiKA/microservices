package com.kdob.songservice.controller;

import com.kdob.songservice.constraint.PositiveIntegerId;
import com.kdob.songservice.constraint.ValidCsvId;
import com.kdob.songservice.dto.request.CreateSongMetadataRequestDto;
import com.kdob.songservice.dto.response.CreateSongMetadataResponseDto;
import com.kdob.songservice.dto.response.DeletedSongMetadataResponseDto;
import com.kdob.songservice.dto.response.GetSongMetadataResponseDto;
import com.kdob.songservice.facade.SongMetadataFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
@AllArgsConstructor
@Validated
public class SongMetadataController {

    private final SongMetadataFacade songMetadataFacade;

    @PostMapping
    public ResponseEntity<CreateSongMetadataResponseDto> createSongMetadata(@Valid @RequestBody final CreateSongMetadataRequestDto createSongMetadataRequestDto) {
        return ResponseEntity.ok(songMetadataFacade.createSongMetadata(createSongMetadataRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetSongMetadataResponseDto> getSongMetadataById(@PathVariable @PositiveIntegerId final String id) {
        return ResponseEntity.ok(songMetadataFacade.getSongMetadata(id));
    }

    @DeleteMapping
    public ResponseEntity<DeletedSongMetadataResponseDto> deleteSongsMetadata(@RequestParam @ValidCsvId final String id) {
        return ResponseEntity.ok(songMetadataFacade.deleteSongMetadata(id));
    }
}
