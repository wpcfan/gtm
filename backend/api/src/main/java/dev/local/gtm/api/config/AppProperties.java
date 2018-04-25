package dev.local.gtm.api.config;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

/**
 * 为本应用服务提供外部可配置的属性支持
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Value
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final LeanCloud leanCloud = new LeanCloud();
    private final SmsCaptcha captcha = new SmsCaptcha();
    private final SmsCode smsCode = new SmsCode();
    private final Security security = new Security();
    private final CorsConfiguration cors = new CorsConfiguration();
    private final Http http = new Http();
    private final UserDefaults userDefaults = new UserDefaults();

    public CorsConfiguration getCors() {
        return cors;
    }

    @Data
    public static class LeanCloud {
        private String appId = "pqmaqNUAzliIxr2yqIw9lY3s-gzGzoHsz";
        private String appKey = "EU4D2VJRUwOx44bwc6tduykw";
    }

    @Data
    public static class SmsCaptcha {
        private String requestUrl = "https://pqmaqnua.api.lncld.net/1.1/requestCaptcha";
        private String verificationUrl = "https://pqmaqnua.api.lncld.net/1.1/verifyCaptcha";
    }

    @Data
    public static class SmsCode {
        private String requestUrl = "https://pqmaqnua.api.lncld.net/1.1/requestSmsCode";
        private String verificationUrl = "https://pqmaqnua.api.lncld.net/1.1/verifySmsCode";
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
            private long tokenValidityInSeconds = 7200;
            private String tokenPrefix = "Bearer ";
        }
    }

    @Data
    public static class Http {
        public enum Version {V_1_1, V_2_0}
        private Version version;
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
}
