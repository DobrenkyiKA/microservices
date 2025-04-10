package com.kdob.resourceservice.dto.request;

import lombok.Data;

@Data
public class SongMetadataRequestDto {
    private long id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
