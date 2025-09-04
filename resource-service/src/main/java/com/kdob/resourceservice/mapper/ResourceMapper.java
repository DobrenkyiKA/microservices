package com.kdob.resourceservice.mapper;

import com.kdob.resourceservice.dao.ResourceInfoDao;
import com.kdob.resourceservice.dto.request.CreateResourceRequestDto;
import com.kdob.resourceservice.dto.response.CreateResourceResponseDto;
import com.kdob.resourceservice.dto.response.DeletedResourcesResponseDto;
import com.kdob.resourceservice.pojo.Resource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    Resource dtoToPojo(CreateResourceRequestDto createResourceRequestDto);

    CreateResourceResponseDto pojoToDto(Resource resource);

    ResourceInfoDao daoToDto(Resource resource);

    default DeletedResourcesResponseDto idsToDto(final List<Long> ids) {
        if (ids == null) {
            return null;
        }
        final DeletedResourcesResponseDto dto = new DeletedResourcesResponseDto();
        dto.setIds(ids);
        return dto;
    }
}
