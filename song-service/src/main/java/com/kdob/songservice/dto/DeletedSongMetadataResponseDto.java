package com.kdob.songservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeletedSongMetadataResponseDto {
    private List<Long> ids;
}
