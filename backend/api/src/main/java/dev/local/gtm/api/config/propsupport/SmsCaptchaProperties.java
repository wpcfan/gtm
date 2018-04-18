package dev.local.gtm.api.config.propsupport;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 为 Captcha 服务提供外部可配置的属性支持
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Component
@ConfigurationProperties("app.sms.captcha")
@Data
public class SmsCaptchaProperties {
    private String requestUrl = "https://pqmaqnua.api.lncld.net/1.1/requestCaptcha";
    private String verificationUrl = "https://pqmaqnua.api.lncld.net/1.1/verifyCaptcha";
}
