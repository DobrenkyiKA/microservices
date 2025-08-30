package com.kdob.resourceservice.service;

import com.kdob.resourceservice.dto.request.SongMetadataRequestDto;
import com.kdob.resourceservice.pojo.Resource;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class SongMetadataIntegrationService {

    private final RestTemplate restTemplate;
    private final EurekaClient discoveryClient;

    @Value("${song.service.application.name}")
    private String applicationName;


    public void createSongMetadata(final Resource resource) {
        final String songServiceUrl = discoveryClient.getNextServerFromEureka(applicationName, false).getHomePageUrl();
        try (InputStream inputStream = new ByteArrayInputStream(resource.getResource())) {

            final Metadata metadata = new Metadata();

            new Mp3Parser().parse(inputStream, new BodyContentHandler(), metadata, new ParseContext());

            final SongMetadataRequestDto metadataDto = new SongMetadataRequestDto();
            metadataDto.setId(resource.getId());
            metadataDto.setName(metadata.get("dc:title"));
            metadataDto.setArtist(metadata.get("xmpDM:artist"));
            metadataDto.setAlbum(metadata.get("xmpDM:album"));
            metadataDto.setYear(metadata.get("xmpDM:releaseDate"));
            metadataDto.setDuration(getDuration(metadata));

            final ResponseEntity<String> response = restTemplate.postForEntity(songServiceUrl, metadataDto, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
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
        final String songServiceUrl = discoveryClient.getNextServerFromEureka(applicationName, false).getHomePageUrl();
        try {
            restTemplate.delete(songServiceUrl + "?id=" + id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting song metadata with ids: " + id, e);
        }

    }
}
