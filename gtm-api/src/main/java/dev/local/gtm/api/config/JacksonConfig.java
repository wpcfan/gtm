package dev.local.gtm.api.config;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

/**
 * 为 zalando problem 配置 Jackson
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Configuration
public class JacksonConfig {
    /*
     * 使用 Jackson Afterburner 模块加速 序列化和反序列化过程
     */
    @Bean
    public AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

    /*
     * 用于序列化和反序列化 RFC7807 Problem 对象的模块。
     */
    @Bean
    ProblemModule problemModule() {
        return new ProblemModule();
    }

    /*
     * 用于序列化和反序列化 ConstraintViolationProblem 的模块
     */
    @Bean
    ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }
}
