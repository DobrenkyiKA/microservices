package com.kdob.resourceservice.dto;

import lombok.Data;

@Data
public class SongMetadataDto {
    private long id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
