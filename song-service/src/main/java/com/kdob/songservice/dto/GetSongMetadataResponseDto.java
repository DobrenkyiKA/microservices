package com.kdob.songservice.dto;

import lombok.Data;

@Data
public class GetSongMetadataResponseDto {

    private long id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
