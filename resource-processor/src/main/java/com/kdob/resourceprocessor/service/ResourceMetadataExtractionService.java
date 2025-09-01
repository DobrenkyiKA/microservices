package com.kdob.resourceprocessor.service;

import com.kdob.resourceprocessor.dto.ResourceDto;
import com.kdob.resourceprocessor.dto.request.SongMetadataRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ResourceMetadataExtractionService {
    public SongMetadataRequestDto createSongMetadata(final ResourceDto resource) {
        try (InputStream inputStream = new ByteArrayInputStream(resource.resource())) {
            final Metadata metadata = new Metadata();
            new Mp3Parser().parse(inputStream, new BodyContentHandler(), metadata, new ParseContext());
            return createMetadata(resource, metadata);
        } catch (final Exception e) {
            throw new RuntimeException("Error retrieving metadata", e);
        }
    }

    private static SongMetadataRequestDto createMetadata(final ResourceDto resource, final Metadata metadata) {
        final SongMetadataRequestDto metadataDto = new SongMetadataRequestDto();
        metadataDto.setId(resource.id());
        metadataDto.setName(metadata.get("dc:title"));
        metadataDto.setArtist(metadata.get("xmpDM:artist"));
        metadataDto.setAlbum(metadata.get("xmpDM:album"));
        metadataDto.setYear(metadata.get("xmpDM:releaseDate"));
        metadataDto.setDuration(getDuration(metadata));
        return metadataDto;
    }

    private static String getDuration(final Metadata metadata) {
        double duration = Double.parseDouble(metadata.get("xmpDM:duration"));
        return String.format("%02d:%02d", (int) (duration / 60), (int) (duration % 60));
    }
}
