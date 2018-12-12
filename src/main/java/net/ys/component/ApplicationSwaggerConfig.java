package net.ys.component;

import org.springframework.context.annotation.Bean;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class ApplicationSwaggerConfig {
    @Bean
    public Docket addUserDocket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        ApiInfo apiInfo = new ApiInfo("REST API", "API文档管理", "V3.8.0", "www.yx.net", "postmaster@yx.net", "license", "#");
        docket.apiInfo(apiInfo);
        return docket;
    }
}
