package ca.jent.javafunwithtypes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestJavaFunWithTypesApplication {

	@Bean
	@ServiceConnection
	@RestartScope
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1-alpine"));
	}

	public static void main(String[] args) {
		SpringApplication
				.from(JavaFunWithTypesApplication::main)
				.with(TestJavaFunWithTypesApplication.class)
				.run(args);
	}

}
