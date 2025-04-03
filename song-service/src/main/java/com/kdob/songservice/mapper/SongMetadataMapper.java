package com.kdob.songservice.mapper;

import com.kdob.songservice.dao.SongMetadataDao;
import com.kdob.songservice.dto.CreateSongMetadataRequestDto;
import com.kdob.songservice.dto.CreateSongMetadataResponseDto;
import com.kdob.songservice.dto.GetSongMetadataResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMetadataMapper {
    SongMetadataDao daoToDto(CreateSongMetadataRequestDto dao);

    CreateSongMetadataResponseDto dtoToDao(SongMetadataDao dao);

    GetSongMetadataResponseDto daoToGetSongMetadataDto(SongMetadataDao songMetadata);
}
