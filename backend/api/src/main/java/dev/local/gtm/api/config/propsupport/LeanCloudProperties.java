package dev.local.gtm.api.config.propsupport;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 为应用配置 LeanCloud 外部属性
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Component
@ConfigurationProperties("app.leancloud")
@Data
public class LeanCloudProperties {
    private String appId = "pqmaqNUAzliIxr2yqIw9lY3s-gzGzoHsz";
    private String appKey = "EU4D2VJRUwOx44bwc6tduykw";
}
