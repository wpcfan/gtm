package dev.local.gtm.api.repository;

import dev.local.gtm.api.domain.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepo extends MongoRepository<Authority, String> {
    Optional<Authority> findOneByName(String name);
}
