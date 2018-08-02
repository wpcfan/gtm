package dev.local.gtm.api.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import dev.local.gtm.api.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableMongoRepositories(basePackages = Constants.BASE_PACKAGE_NAME + ".repository.mongo")
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfig {

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateTimeUtil.DateToZonedDateTimeConverter.INSTANCE);
        converters.add(DateTimeUtil.ZonedDateTimeToDateConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    @Bean
    public Mongobee mongobee(
        MongoClient mongoClient,
        MongoTemplate mongoTemplate,
        MongoProperties mongoProperties,
        Environment environment) {
            log.debug("配置 Mongobee");
            Mongobee mongobee = new Mongobee(mongoClient);
            mongobee.setDbName(mongoProperties.getDatabase());
            mongobee.setMongoTemplate(mongoTemplate);
            // package to scan for migrations
            mongobee.setChangeLogsScanPackage(Constants.BASE_PACKAGE_NAME + ".changelogs");
            mongobee.setSpringEnvironment(environment);
            mongobee.setEnabled(true);
            return mongobee;
    }
}
