package org.evlove.common.bom.service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initial configuration of SpringDoc
 *
 * @author massaton.github.io
 */
@Configuration
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class ApiDocumentConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${spring.application.version}")
    private String projectVersion;

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(serviceName + " service")
                        .description("")
                        .version(projectVersion)
                        //.contact(new Contact().name("Massless").email("massaton.fi@gmail.com").url("https://massaton.github.io"))
                        //.license(new License().name("MIT").url("https://mit-license.org/"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Massless")
                        .url("https://massaton.github.io")
                );
    }
}
