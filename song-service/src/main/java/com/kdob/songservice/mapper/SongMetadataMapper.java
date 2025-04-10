package com.kdob.songservice.mapper;

import com.kdob.songservice.dao.SongMetadataDao;
import com.kdob.songservice.dto.request.CreateSongMetadataRequestDto;
import com.kdob.songservice.dto.response.CreateSongMetadataResponseDto;
import com.kdob.songservice.dto.response.GetSongMetadataResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMetadataMapper {
    SongMetadataDao daoToDto(CreateSongMetadataRequestDto dao);

    CreateSongMetadataResponseDto dtoToDao(SongMetadataDao dao);

    GetSongMetadataResponseDto daoToGetSongMetadataDto(SongMetadataDao songMetadata);
}
