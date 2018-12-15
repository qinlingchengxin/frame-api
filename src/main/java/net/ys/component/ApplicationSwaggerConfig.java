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
        ApiInfo apiInfo = new ApiInfo("REST API", "<a href=\"/frame-api/global/api/export\">API文档</a>", "V3.8.0", "www.yx.net", "postmaster@yx.net", "result code", "/frame-api/global/result/code");
        docket.apiInfo(apiInfo);
        return docket;
    }
}
