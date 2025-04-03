package com.kdob.resourceservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeletedResourcesResponseDto {
    private List<Long> ids;
}
