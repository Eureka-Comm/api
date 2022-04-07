package com.castellanos94.fuzzylogic.api.db;


import com.castellanos94.fuzzylogic.api.model.users.ERole;
import com.castellanos94.fuzzylogic.api.model.users.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}