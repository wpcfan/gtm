package dev.local.gtm.api.config;

import dev.local.gtm.api.config.resttemplate.LeanCloudAuthHeaderInterceptor;
import dev.local.gtm.api.config.resttemplate.LeanCloudRequestErrorHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 为应用访问外部 Http Rest API 提供配置
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class OutgoingRestTemplateConfig {

    private static final int TIMEOUT = 10000;
    private final AppProperties appProperties;

    @Bean("leanCloudTemplate")
    public RestTemplate getLeanCloudRestTemplate() {
        return new RestTemplateBuilder().setConnectTimeout(TIMEOUT).setReadTimeout(TIMEOUT)
                .errorHandler(new LeanCloudRequestErrorHandler())
                // .requestFactory(new HttpComponentsAsyncClientHttpRequestFactory()) // Use
                // Apache HttpComponent
                .interceptors(new LeanCloudAuthHeaderInterceptor(appProperties)) // new RequestLoggingInterceptor()
                .build();
    }
}
