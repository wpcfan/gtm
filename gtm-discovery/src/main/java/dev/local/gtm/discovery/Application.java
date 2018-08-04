package dev.local.gtm.discovery;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务发现服务器
 */
@SpringBootApplication
@EnableAdminServer
@EnableEurekaServer
@RefreshScope
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
