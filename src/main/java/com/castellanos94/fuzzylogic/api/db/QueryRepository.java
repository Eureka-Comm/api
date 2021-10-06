package com.castellanos94.fuzzylogic.api.db;


import com.castellanos94.fuzzylogic.api.model.Query;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface QueryRepository extends MongoRepository<Query, String> {

}
