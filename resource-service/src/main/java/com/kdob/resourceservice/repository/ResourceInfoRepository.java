package com.kdob.resourceservice.repository;

import com.kdob.resourceservice.dao.ResourceInfoDao;
import org.springframework.data.repository.CrudRepository;


public interface ResourceInfoRepository extends CrudRepository<ResourceInfoDao, Long> {
}
