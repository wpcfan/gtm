package dev.local.gtm.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.sms.captcha")
@Data
public class SmsCaptchaProperties {
    private String appId = "pqmaqNUAzliIxr2yqIw9lY3s-gzGzoHsz";
    private String appKey = "EU4D2VJRUwOx44bwc6tduykw";
    private String requestUrl = "https://pqmaqnua.api.lncld.net/1.1/requestCaptcha";
    private String verificationUrl = "https://pqmaqnua.api.lncld.net/1.1/verifyCaptcha";
}
