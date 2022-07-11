package com.castellanos94.fuzzylogic.api.db;


import com.castellanos94.fuzzylogicgp.core.ResultTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


public interface EurekaTaskRepository extends MongoRepository<EurekaTask, String> {
    List<EurekaTask> findByUserId(String userId);

    @Query(value = "{ 'query._class' : ?0 }", fields = "{'resultTask':0}")
    Page<EurekaTask> findByTaskType(String className, Pageable pageable);

    @Query(value = "{ 'query._class' : ?0 , 'query.isPublic':true}", fields = "{'id':0,'userId':0, 'resultTask':0}")
    Page<EurekaTask> findPublicByTaskType(String className, Pageable pageable);

    @Query(value = "{ 'id' : ?0 }", fields = "{'resultTask':1, 'id':1, 'status':1}")
    Optional<EurekaTask> findResultTaskById(String id);
}
