package com.kdob.songservice.repository;

import com.kdob.songservice.dao.SongMetadataDao;
import org.springframework.data.repository.CrudRepository;

public interface SongMetadataRepository extends CrudRepository<SongMetadataDao, Long> {
}
