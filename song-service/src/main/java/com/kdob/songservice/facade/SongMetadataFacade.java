package com.kdob.songservice.facade;

import com.kdob.songservice.dao.SongMetadataDao;
import com.kdob.songservice.dto.request.CreateSongMetadataRequestDto;
import com.kdob.songservice.dto.response.CreateSongMetadataResponseDto;
import com.kdob.songservice.dto.response.DeletedSongMetadataResponseDto;
import com.kdob.songservice.dto.response.GetSongMetadataResponseDto;
import com.kdob.songservice.mapper.SongMetadataMapper;
import com.kdob.songservice.service.SongMetadataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SongMetadataFacade {

    private final SongMetadataService songMetadataService;
    private final SongMetadataMapper songMetadataMapper;

    public CreateSongMetadataResponseDto createSongMetadata(final CreateSongMetadataRequestDto request) {
        final SongMetadataDao songMetadataToCreate = songMetadataMapper.daoToDto(request);
        final SongMetadataDao createdSongMetadata = songMetadataService.createSongMetadata(songMetadataToCreate);
        return songMetadataMapper.dtoToDao(createdSongMetadata);
    }

    public GetSongMetadataResponseDto getSongMetadata(final Integer id) {
        return songMetadataMapper.daoToGetSongMetadataDto(songMetadataService.getSongMetadata(id));
    }

    public DeletedSongMetadataResponseDto deleteSongMetadata(final String id) {
        return songMetadataService.deleteSongMetadata(id);
    }
}
