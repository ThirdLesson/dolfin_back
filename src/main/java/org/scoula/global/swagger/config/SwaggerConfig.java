package org.scoula.global.swagger.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private final String API_NAME = "dolfin API";
	private final String API_VERSION = "1.0";
	private final String API_DESCRIPTION = "dolfin API 명세서";

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title(API_NAME)
			.description(API_DESCRIPTION)
			.version(API_VERSION)
			.build();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo())
			.securityContexts(Collections.singletonList(securityContext())) 
			.securitySchemes(List.of(apiKey()))
			.ignoredParameterTypes(CustomUserDetails.class, Authentication.class );

	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything"); 
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return List.of(new SecurityReference("JWT", authorizationScopes)); 
	}

	private ApiKey apiKey() {
		return new ApiKey("JWT", "Authorization", "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.forPaths(PathSelectors.any())
			.build();
	}

}
