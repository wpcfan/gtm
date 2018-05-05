package dev.local.gtm.api.repository.mongo;

import dev.local.gtm.api.domain.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends MongoRepository<Authority, String> {
    Optional<Authority> findOneByName(String name);
}
