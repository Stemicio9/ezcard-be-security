package com.metra.ezcardbesecurity.respository;

import com.metra.ezcardbesecurity.entity.UserEz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEzRepository extends MongoRepository<UserEz, String> {
    Optional<UserEz> findByUsername(String username);


}
