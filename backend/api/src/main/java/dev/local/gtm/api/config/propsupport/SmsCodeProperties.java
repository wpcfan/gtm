package dev.local.gtm.api.config.propsupport;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 为短信验证码提供外部可配置的属性支持
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Component
@ConfigurationProperties("app.sms.code")
@Data
public class SmsCodeProperties {
    private String requestUrl = "https://pqmaqnua.api.lncld.net/1.1/requestSmsCode";
    private String verificationUrl = "https://pqmaqnua.api.lncld.net/1.1/verifySmsCode";
}
