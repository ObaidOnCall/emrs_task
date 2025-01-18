package io.hahn_software.emrs.conf;

import io.swagger.v3.oas.models.servers.Server;
import io.hahn_software.emrs.utils.PropertiesLoader;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UtilsConf {
    
    @Bean("propertiesLoader1")
    public PropertiesLoader propertiesLoader() {
        return PropertiesLoader.builder().build() ;
    }



    /***
     * 
     * swagger config
     */


     @SuppressWarnings("unchecked")
    @Bean
    public OpenAPI customOpenAPI() {
    
    final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
            .info(new Info().title("Employee Records Management System")
                    .description("An internal Employee Records Management System to centralize the management of employee data across departments.")
                    .version("v0.0.1")
                    .license(new License().name("Apache 2.0").url("http://springdoc.org")))
            .externalDocs(new ExternalDocumentation()
                    .description("SpringBoot Wiki Documentation")
                    .url("https://springboot.wiki.github.org/docs"))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(
                    new Components()
                            .addSecuritySchemes(securitySchemeName,
                                    new SecurityScheme()
                                            .name(securitySchemeName)
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                            )
                            .addRequestBodies("FileUploadRequest",
                            new RequestBody()
                            .content(new Content()
                                    .addMediaType("multipart/form-data",
                                    new MediaType()
                                            .schema(new Schema<>()
                                            .type("array")
                                            .items(new Schema<>()
                                                    .type("string")
                                                    .format("binary"))))))
            ) ;
    }
}
