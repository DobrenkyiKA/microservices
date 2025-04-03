package com.kdob.resourceservice.repository;

import com.kdob.resourceservice.dao.ResourceDao;
import org.springframework.data.repository.CrudRepository;


public interface ResourceRepository extends CrudRepository<ResourceDao, Long> {
}
