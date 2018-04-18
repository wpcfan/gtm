package dev.local.gtm.api.repository;

import dev.local.gtm.api.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 任务存储接口
 */
@Repository
public interface TaskRepo extends MongoRepository<Task, String> {
    Page<Task> findByDescLike(Pageable pageable, @Param("desc") String desc);

    Page<Task> findByOwnerMobile(Pageable pageable, @Param("mobile") String mobile);
}
