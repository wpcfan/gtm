package dev.local.gtm.api.repository.mongo;

import dev.local.gtm.api.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 用户存储接口
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_MOBILE_CACHE = "usersByMobile";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByMobile(@Param("mobile") String mobile);

    Optional<User> findOneByEmailIgnoreCase(@Param("email") String email);

    Optional<User> findOneByLoginIgnoreCase(@Param("login") String login);

    @Cacheable(cacheNames = USERS_BY_MOBILE_CACHE)
    int countByMobile(@Param("login") String login);

    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    int countByEmailIgnoreCase(@Param("email") String email);

    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    int countByLoginIgnoreCase(@Param("login") String login);

    Page<User> findAllByLoginNot(Pageable pageable, @Param("login") String login);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);
}
