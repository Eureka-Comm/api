package com.castellanos94.fuzzylogic.api.db;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface EurekaTaskRepository extends MongoRepository<EurekaTask, String> {
    List<EurekaTask> findByUserId(String userId);
}
