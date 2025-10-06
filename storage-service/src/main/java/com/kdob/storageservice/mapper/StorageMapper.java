package com.kdob.storageservice.mapper;

import com.kdob.storageservice.dto.StorageRequestDto;
import com.kdob.storageservice.dto.StorageResponseDto;
import com.kdob.storageservice.model.Storage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StorageMapper {
    
    Storage toEntity(StorageRequestDto requestDto);
    
//    StorageResponseDto toResponseDto(Storage storage);
    
    List<StorageResponseDto> toResponseDtoList(List<Storage> storages);
}