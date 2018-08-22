package dev.local.gtm.api.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

/**
 * 为本应用服务提供外部可配置的属性支持
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final LeanCloud leanCloud = new LeanCloud();
    private final SmsCaptcha captcha = new SmsCaptcha();
    private final SmsCode smsCode = new SmsCode();
    private final Security security = new Security();
    private final CorsConfiguration cors = new CorsConfiguration();
    private final Http http = new Http();
    private final UserDefaults userDefaults = new UserDefaults();
    private final RedissonConfig redissonConfig = new RedissonConfig();
    private final Async async = new Async();

    @Data
    public static class LeanCloud {
        private String appId = "aYoLLIK88BIjjR4HeoxzGp5A-gzGzoHsz";
        private String appKey = "rr56zkrXHunG5aNzcScSVi0d";
    }

    @Data
    public static class SmsCaptcha {
        private String requestUrl = "https://ayollik8.api.lncld.net/1.1/requestCaptcha";
        private String verificationUrl = "https://ayollik8.api.lncld.net/1.1/verifyCaptcha";
    }

    @Data
    public static class SmsCode {
        private String requestUrl = "https://ayollik8.api.lncld.net/1.1/requestSmsCode";
        private String verificationUrl = "https://ayollik8.api.lncld.net/1.1/verifySmsCode";
    }

    @Data
    public static class Security {
        private final Jwt jwt = new Jwt();
        private final Authorization authorization = new Authorization();

        @Data
        public static class Authorization {
            private String header = "Authorization";
        }

        @Data
        public static class Jwt {
            private String secret = "myDefaultSecret";
            private String refershSecret = "myDefaultRefreshSecret";
            private long tokenValidityInSeconds = 7200;
            private long refreshTokenValidityInSeconds = 2592000;
            private String tokenPrefix = "Bearer ";
        }
    }

    @Data
    public static class Http {
        public enum Version {
            V_1_1, V_2_0
        }

        private Version version = Version.V_1_1;
        private final Cache cache = new Cache();

        @Data
        public static class Cache {
            private int timeToLiveInDays = 1461;
        }
    }

    @Data
    public static class UserDefaults {
        private String initialPassword = "Abcd@1234Ef";
    }

    @Data
    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 50;
        private int queueCapacity = 10000;
    }

    @Data
    public static class RedissonConfig {
        private String address = "redis://redis:6379";
        private int connectionMinimumIdleSize = 10;
        private int idleConnectionTimeout = 10000;
        private int pingTimeout = 1000;
        private int connectTimeout = 10000;
        private int timeout = 3000;
        private int retryAttempts = 3;
        private int retryInterval = 1500;
        private String password = null;
        private int subscriptionsPerConnection = 5;
        private String clientName = null;
        private int subscriptionConnectionMinimumIdleSize = 1;
        private int subscriptionConnectionPoolSize = 50;
        private int connectionPoolSize = 64;
        private int database = 0;
        private boolean dnsMonitoring = false;
        private int dnsMonitoringInterval = 5000;

        private int thread = 2;

        private String codec = "org.redisson.codec.JsonJacksonCodec";
    }
}
