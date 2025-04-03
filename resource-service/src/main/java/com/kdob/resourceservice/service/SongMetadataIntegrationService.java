package com.kdob.resourceservice.service;

import com.kdob.resourceservice.dao.ResourceDao;
import com.kdob.resourceservice.dto.SongMetadataDto;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class SongMetadataIntegrationService {

    private final RestTemplate restTemplate;
    private static final String SONG_SERVICE_URL = "http://localhost:8082/songs";

    public void createSongMetadata(final ResourceDao resource) {
        try (InputStream inputStream = new ByteArrayInputStream(resource.getResource())) {

            final Metadata metadata = new Metadata();

            new Mp3Parser().parse(inputStream, new BodyContentHandler(), metadata, new ParseContext());

            final SongMetadataDto metadataDto = new SongMetadataDto();
            metadataDto.setId(resource.getId());
            metadataDto.setName(metadata.get("dc:title"));
            metadataDto.setArtist(metadata.get("xmpDM:artist"));
            metadataDto.setAlbum(metadata.get("xmpDM:album"));
            metadataDto.setYear(metadata.get("xmpDM:releaseDate"));
            metadataDto.setDuration(getDuration(metadata));

            ResponseEntity<String> response = restTemplate.postForEntity(SONG_SERVICE_URL, metadataDto, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                // Handle failure response appropriately
                throw new RuntimeException("Failed to send metadata: " + response.getStatusCode());
            }

        } catch (final Exception e) {
            throw new RuntimeException("Error retrieving and sending MP3 metadata", e);
        }
    }

    private static String getDuration(final Metadata metadata) {
        double duration = Double.parseDouble(metadata.get("xmpDM:duration"));
        return String.format("%02d:%02d", (int) (duration / 60), (int) (duration % 60));
    }

    public void deleteSongMetadata(final String id) {
        try {
            restTemplate.delete(SONG_SERVICE_URL + "?id=" +id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting song metadata with ids: " + id, e);
        }

    }
}
