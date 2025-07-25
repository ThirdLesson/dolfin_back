package org.scoula.global.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * RootConfig: 비즈니스 로직 (Service, Mapper, DataSource)
 * 와일드카드 패턴 사용 (**)
 *
 * org.scoula.domain.**.mapper - 모든 도메인의 mapper 자동 스캔
 * org.scoula.domain.**.service - 모든 도메인의 service 자동 스캔
 * org.scoula.domain.**.controller - 모든 도메인의 controller 자동 스캔
 */
@Configuration
@PropertySource({"classpath:/application.properties"})
@MapperScan(basePackages = {"org.scoula.domain.**.mapper"}) // 게시판과 회원 도메인 매퍼 스캔
@ComponentScan(basePackages = {
	// "org.scoula.service",
	"org.scoula.domain.**.service",  // 도메인 객체를 포함하기 위해 추가
	"org.scoula.global.swagger.config",  // Swagger 설정을 포함하기 위해 추가
	"org.scoula.global.kafka", // kafka 설정 포함
	"org.scoula.global.exception", // exception handler 등록
	"org.scoula.global.redis", // 공통 설정 포함
	"org.scoula.global.security", // security 설정 포함
})
public class RootConfig {
	@Value("${jdbc.driver}")
	String driver;
	@Value("${jdbc.url}")
	String url;
	@Value("${jdbc.username}")
	String username;
	@Value("${jdbc.password}")
	String password;

	@Bean
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setMaximumPoolSize(50);
		config.setMinimumIdle(5);
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);
		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public Flyway flyway(DataSource dataSource) {
		Flyway flyway = Flyway.configure()
			.dataSource(dataSource)
			.locations("classpath:db/migration")
			.baselineVersion("0.0")  // 초기 마이그레이션 버전 설정
			.baselineOnMigrate(true)
			.encoding("UTF-8")
			.cleanDisabled(true)  // 안전을 위해 clean 비활성화
			.validateMigrationNaming(true)
			.load();
		// Flyway 설정

		// 수동으로 migrate 호출
		flyway.migrate();

		return flyway;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setConfigLocation(
			applicationContext.getResource("classpath:/mybatis-config.xml")
		);
		sqlSessionFactory.setDataSource(dataSource());
		return sqlSessionFactory.getObject();
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
		return new HandlerMappingIntrospector();
	}
}
