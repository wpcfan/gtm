package dev.local.gtm.api.config;

public class AppConstants {
    // Spring profiles for development, test and production, see http://www.jhipster.tech/profiles/
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    public static final String SPRING_PROFILE_CLOUD = "cloud";
    // Spring profile used when deploying to Heroku
    public static final String SPRING_PROFILE_HEROKU = "heroku";
    // Spring profile used when deploying to Amazon ECS
    public static final String SPRING_PROFILE_AWS_ECS = "aws-ecs";
    // Spring profile used to disable swagger
    public static final String SPRING_PROFILE_SWAGGER = "swagger";
    // Spring profile used to disable running liquibase
    public static final String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
    // Spring profile used when deploying to Kubernetes and OpenShift
    public static final String SPRING_PROFILE_K8S = "k8s";
}
