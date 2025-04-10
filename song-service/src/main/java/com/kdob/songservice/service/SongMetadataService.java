package com.kdob.songservice.service;

import com.kdob.songservice.dao.SongMetadataDao;
import com.kdob.songservice.dto.response.DeletedSongMetadataResponseDto;
import com.kdob.songservice.exception.AlreadyExistSongMetadataException;
import com.kdob.songservice.exception.NoSuchSongMetadataException;
import com.kdob.songservice.repository.SongMetadataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service
public class SongMetadataService {

    private final SongMetadataRepository songMetadataRepository;

    public SongMetadataDao createSongMetadata(final SongMetadataDao request) {
        if (songMetadataRepository.existsById(request.getId())) {
            throw new AlreadyExistSongMetadataException("Metadata for resource ID=" + request.getId() + " already exists");
        }
        return songMetadataRepository.save(request);
    }

    public SongMetadataDao getSongMetadata(final long id) {
        return songMetadataRepository.findById(id).orElseThrow(() -> new NoSuchSongMetadataException("Song metadata for ID=" + id + " not found"));
    }

    public DeletedSongMetadataResponseDto deleteSongMetadata(final String id) {
        List<String> ids = Arrays.asList(id.split(","));
        List<Long> array = ids.stream()
                .map(Long::parseLong)
                .toList();

        List<Long> result = new ArrayList<>();
        for (Long aLong : array) {
            if (songMetadataRepository.existsById(aLong)) {
                songMetadataRepository.deleteById(aLong);
                result.add(aLong);
            }
        }

        DeletedSongMetadataResponseDto resourceDeleteDto = new DeletedSongMetadataResponseDto();
        resourceDeleteDto.setIds(result);
        return resourceDeleteDto;
    }
}
