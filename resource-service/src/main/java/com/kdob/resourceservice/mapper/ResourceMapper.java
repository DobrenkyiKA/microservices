package com.kdob.resourceservice.mapper;

import com.kdob.resourceservice.dao.ResourceDao;
import com.kdob.resourceservice.dto.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.CreateResourceResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

//    @Mapping(target = "resource", source = "createResourceRequestDto.resource")
    ResourceDao dtoToDao(CreateResourceRequestDto createResourceRequestDto);
    CreateResourceResponseDto daoToDto(ResourceDao resourceDao);
}
