package com.kdob.resourceservice.mapper;

import com.kdob.resourceservice.dao.ResourceDao;
import com.kdob.resourceservice.dto.request.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.response.CreateResourceResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceDao dtoToDao(CreateResourceRequestDto createResourceRequestDto);

    CreateResourceResponseDto daoToDto(ResourceDao resourceDao);
}
