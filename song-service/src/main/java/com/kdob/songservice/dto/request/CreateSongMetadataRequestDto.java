package com.kdob.songservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSongMetadataRequestDto {

    private long id;
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @NotNull(message = "Name must be provided")
    private String name;
    @NotNull(message = "Artist must be provided")
    @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
    private String artist;
    @Size(min = 1, max = 100, message = "Album must be between 1 and 100 characters")
    @NotNull(message = "Album must be provided")
    private String album;
    @Pattern(regexp = "^\\d{2}:[0-5][0-9]$", message = "Duration must be in mm:ss format with leading zeros")
    @NotNull(message = "Duration must be provided")
    private String duration;
    @Pattern(regexp = "^(19|20)[0-9]{2}$", message = "Year must be between 1900 and 2099")
    @NotNull(message = "Year must be provided")
    private String year;
}
