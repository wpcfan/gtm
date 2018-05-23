package dev.local.gtm.api.config;

import io.swagger.models.auth.In;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.classmate.TypeResolver;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * 配置 Swagger 以提供 API 文档
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@EnableSwagger2
@ComponentScan(basePackages = Constants.BASE_PACKAGE_NAME + ".web.rest")
@Import({ springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class })
@Configuration
public class SwaggerConfig {
    /**
     * 配置 Swagger 扫描哪些 API （不列出那些监控 API）
     *
     * @return Docket
     */
    @Bean
    public Docket apiDoc() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage(Constants.BASE_PACKAGE_NAME + ".web.rest")).paths(PathSelectors.any())
                .build().pathMapping("/").securitySchemes(newArrayList(apiKey()))
                .securityContexts(newArrayList(securityContext())).directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class).apiInfo(apiInfo());

    }

    @Bean
    public AlternateTypeRuleConvention myPageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return newArrayList(newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin())));
            }
        };
    }

    private Type pageableMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(String.format("%s.generated.%s", Pageable.class.getPackage().getName(),
                        Pageable.class.getSimpleName()))
                .withProperties(newArrayList(property(Integer.class, "page"), property(Integer.class, "size"),
                        property(String.class, "sort")))
                .build();
    }

    private AlternateTypePropertyBuilder property(Class<?> type, String name) {
        return new AlternateTypePropertyBuilder().withName(name).withType(type).withCanRead(true).withCanWrite(true);
    }

    private ApiKey apiKey() {
        // 用于Swagger UI测试时添加Bearer Token
        return new ApiKey("Bearer", HttpHeaders.AUTHORIZATION, In.HEADER.name());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api/((?!auth).)*")).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(new SecurityReference("Bearer", authorizationScopes));
    }

    /**
     * 对 API 的概要信息进行定制
     *
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfo("GTM API 文档", "所有 GTM 开放的 API 接口，供 Android， iOS 和 Web 客户端调用", "1.0",
                "http://www.twigcodes.com/gtm/tos.html",
                new Contact("推码科技", "http://www.twigcodes.com", "wangpeng@twigcodes.com"), "API 授权协议",
                "http://www.twigcodes.com/gtm/api-license.html", Collections.emptyList());
    }
}
