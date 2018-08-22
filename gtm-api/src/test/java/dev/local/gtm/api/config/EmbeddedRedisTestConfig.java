package dev.local.gtm.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
@Profile("test")
public class EmbeddedRedisTestConfig {

    private final redis.embedded.RedisServer redisServer;

    public EmbeddedRedisTestConfig(@Value("${spring.redis.port}") final int redisPort) throws IOException {
        this.redisServer = new redis.embedded.RedisServer(redisPort);
    }

    @PostConstruct
    public void startRedis() {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }
}
