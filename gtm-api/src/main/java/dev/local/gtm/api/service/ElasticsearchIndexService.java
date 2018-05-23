package dev.local.gtm.api.service;

import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.domain.search.UserSearch;
import dev.local.gtm.api.repository.mongo.UserRepository;
import dev.local.gtm.api.repository.search.UserSearchRepository;
import dev.local.gtm.api.security.AuthoritiesConstants;
import dev.local.gtm.api.web.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ElasticsearchIndexService {

    private static final Lock reindexLock = new ReentrantLock();

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Async
    @Secured(AuthoritiesConstants.ADMIN)
    @Scheduled(cron = "0 0 1 * * ?")
    public void reindexAll() {
        if (reindexLock.tryLock()) {
            try {
                reIndexAll(User.class, UserSearch.class, userRepository, userSearchRepository);
                log.info("Elasticsearch: 重建索引成功");
            } finally {
                reindexLock.unlock();
            }
        } else {
            log.info("Elasticsearch: 并发的重建索引请求");
        }
    }

    private <T, S, ID extends Serializable> void reIndexAll(Class<T> entityClass, Class<S> searchClass,
            MongoRepository<T, ID> mongoRepository, ElasticsearchRepository<S, ID> elasticsearchRepository) {
        if (mongoRepository.count() == 0) {
            return;
        }
        elasticsearchTemplate.deleteIndex(searchClass);
        elasticsearchTemplate.createIndex(searchClass);
        elasticsearchTemplate.putMapping(searchClass);
        final int size = 100;
        for (int i = 0; i <= mongoRepository.count() / size; i++) {
            Pageable page = PageRequest.of(i, size);
            log.info("索引 第 {} 页到第 {} 页, 每页 {} 条记录", i, mongoRepository.count() / size, size);
            Page<T> results = mongoRepository.findAll(page);
            Constructor<S> cons;
            try {
                cons = searchClass.getConstructor(entityClass);
            } catch (NoSuchMethodException e) {
                throw new InternalServerErrorException("Elasticsearch 的实体类缺少以数据库实体为参数的构造函数");
            }
            Constructor<S> finalCons = cons;
            elasticsearchRepository.saveAll(results.map(initargs -> {
                try {
                    return finalCons.newInstance(initargs);
                } catch (Exception e) {
                    throw new InternalServerErrorException("Elasticsearch 的实体类构造函数发生异常");
                }
            }).getContent());
        }
    }
}
