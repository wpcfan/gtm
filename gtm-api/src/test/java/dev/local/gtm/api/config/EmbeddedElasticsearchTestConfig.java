package dev.local.gtm.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
@Profile("test")
public class EmbeddedElasticsearchTestConfig {
    private final EmbeddedElastic embeddedElastic;

    public EmbeddedElasticsearchTestConfig() throws IOException {
        this.embeddedElastic = EmbeddedElastic.builder()
            .withElasticVersion("5.5.0")
            .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9300)
            .withSetting(PopularProperties.CLUSTER_NAME, "my_cluster")
            .withPlugin("analysis-stempel")
            .build();
    }

    @PostConstruct
    public void startRedis() throws IOException, InterruptedException {
        this.embeddedElastic.start();
    }

    @PreDestroy
    public void stopRedis() {
        this.embeddedElastic.stop();
    }
}
