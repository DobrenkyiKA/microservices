package com.kdob.songservice.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "song_metadata")
public class SongMetadataDao {

    @Id
    private long id;
    

    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
