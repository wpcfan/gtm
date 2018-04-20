package dev.local.gtm.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Log4j2
@RequiredArgsConstructor
@Configuration
public class CorsConfig {

    private final AppProperties appProperties;

    @Bean
    public CorsFilter corsFilter() {
        val source = new UrlBasedCorsConfigurationSource();
        val config = appProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("注册 CORS 过滤器");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
            source.registerCorsConfiguration("/*/api/**", config);
            source.registerCorsConfiguration("/*/management/**", config);
        }
        return new CorsFilter(source);
    }
}
