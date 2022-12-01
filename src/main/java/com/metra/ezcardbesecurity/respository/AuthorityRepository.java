package com.metra.ezcardbesecurity.respository;

import com.metra.ezcardbesecurity.entity.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
