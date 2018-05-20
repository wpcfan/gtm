package dev.local.gtm.api.config;

/**
 * 应用的常量，需要在全局可用的常量都应该在这个类中定义
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
public final class Constants {

    // 分别对应开发、测试和生产环境的 Spring 配置
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    // 用户名正则表达式
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    public static final String MOBILE_REGEX = "^1[3456789]\\d{9}$";
    public static final String ID_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "zh-cn";

    private Constants() {
    }
}
