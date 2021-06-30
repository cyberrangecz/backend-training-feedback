package cz.muni.ics.kypo.training.feedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerConfig.class);
    @Value("${swagger.enabled:false}")
    private boolean swaggerEnabled;

    @Bean
    public Docket publicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("public-api")
                .enable(swaggerEnabled)
                .apiInfo(apiInfo()).useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cz.muni.ics.kypo.training.feedback.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("KYPO Training Feedback Service - API Reference")
                .description("Developed by KYPO team")
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }
}