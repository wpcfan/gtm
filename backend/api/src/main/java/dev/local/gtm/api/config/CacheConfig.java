package dev.local.gtm.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 不加这个注解 '@AutoConfigureBefore(value = { DatabaseConfig.class })' 会影响 ElasticSearch
 */
@Log4j2
@RequiredArgsConstructor
@AutoConfigureBefore(value = { SecurityConfig.class, DatabaseConfig.class })
@EnableCaching
@Configuration
public class CacheConfig {

    private final RedisProperties redisProperties;

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() {
        Config config = new Config();
        // sentinel
        if (redisProperties.getSentinel() != null) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig.setMasterName(redisProperties.getSentinel().getMaster());
            val nodes = redisProperties.getSentinel().getNodes();
            sentinelServersConfig.addSentinelAddress(nodes.toArray(new String[0]));
            sentinelServersConfig.setDatabase(redisProperties.getDatabase());
            if (redisProperties.getPassword() != null) {
                sentinelServersConfig.setPassword(redisProperties.getPassword());
            }
        } else { // 单个 Server
            SingleServerConfig singleServerConfig = config.useSingleServer();
            // format as redis://127.0.0.1:7181 or rediss://127.0.0.1:7181 for SSL
            String schema = redisProperties.isSsl() ? "rediss://" : "redis://";
            singleServerConfig.setAddress(schema + redisProperties.getHost() + ":" + redisProperties.getPort());
            singleServerConfig.setDatabase(redisProperties.getDatabase());
            if (redisProperties.getPassword() != null) {
                singleServerConfig.setPassword(redisProperties.getPassword());
            }
        }
        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        log.debug("生成缓存处理器");
        return new RedissonSpringCacheManager(redissonClient);
    }
}
