package dev.local.gtm.api.repository;

import dev.local.gtm.api.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource
public interface UserRepo extends MongoRepository<User, String> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_MOBILE_CACHE = "usersByMobile";

    @Cacheable(cacheNames = USERS_BY_MOBILE_CACHE)
    Optional<User> findOneByMobile(@Param("mobile") String mobile);

    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneByLogin(@Param("login") String login);

    Page<User> findAllByLoginNot(Pageable pageable, @Param("login") String login);
    @RestResource(exported = false)
    @Override
    <S extends User> S insert(S entity);

    @RestResource
    @Override
    Page<User> findAll(Pageable pageable);

    @RestResource(exported = false)
    @Override
    <S extends User> S save(S entity);

    @RestResource
    @Override
    Optional<User> findById(String s);

    @Override
    boolean existsById(String s);

    @Override
    long count();

    @RestResource(exported = false)
    @Override
    void deleteById(String s);
}
