package com.metra.ezcardbesecurity.respository;

import com.metra.ezcardbesecurity.entity.profile.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {

    Optional<Profile> findByUsername(String username);
    Optional<Profile> findById(String id);



}

