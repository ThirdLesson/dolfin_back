package org.scoula.global.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
    "org.scoula.domain.controller",
	"org.scoula.domain.**.controller",
	// "org.scoula.domain.codef.controller",
})
public class ServletConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/resources/**") // url이 /resources/로 시작하는 모든 경로
			.addResourceLocations("/resources/"); // webapp/resources/경로로 매핑

		// Swagger UI 리소스를 위한 핸들러 설정
		registry.addResourceHandler("/swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");

		// Swagger WebJar 리소스 설정
		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");

		// Swagger 리소스 설정
		registry.addResourceHandler("/swagger-resources/**")
			.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/v2/api-docs")
			.addResourceLocations("classpath:/META-INF/resources/");
	}

	// jsp view resolver 설정
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver bean = new InternalResourceViewResolver();
		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/views/");
		bean.setSuffix(".jsp");
		registry.viewResolver(bean);
	}
}
