package org.scoula.global.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.scoula.domain.remittancegroup.batch.reader.RemittanceGroupReader;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
@PropertySource({"classpath:/application.properties"})
@EnableBatchProcessing
@EnableScheduling
@MapperScan(basePackages = {"org.scoula.domain.**.mapper"}) 
@ComponentScan(basePackages = {
	"org.scoula.domain.**.service",  
	"org.scoula.domain.**.util",
	"org.scoula.domain.remittancegroup.batch", 
	"org.scoula.domain.ledger.batch",
	"org.scoula.global.swagger.config",  
	"org.scoula.global.kafka",
	"org.scoula.global.exception", 
	"org.scoula.global.redis", 
	"org.scoula.global.security", 
	"org.scoula.global.firebase", 
	"org.scoula.global.batch.config",
})
@Import({RemittanceGroupReader.class})
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
		config.setMaximumPoolSize(10);
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
			.baselineVersion("0.0")  
			.baselineOnMigrate(true)
			.encoding("UTF-8")
			.cleanDisabled(true) 
			.validateMigrationNaming(true)
			.load();

		flyway.migrate();

		return flyway;
	}

	@Bean
	@Primary
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
