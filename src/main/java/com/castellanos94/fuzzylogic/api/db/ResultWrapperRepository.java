package com.castellanos94.fuzzylogic.api.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ResultWrapperRepository extends MongoRepository<ResultWrapper, String> {
    Optional<ResultWrapper> findByTaskId(String id);

    @Query(value = "{ 'taskId' : ?0 }", fields = "{'result':1, 'id':1}")
    Optional<ResultWrapper> findByTaskIdFilter(String id);
}
