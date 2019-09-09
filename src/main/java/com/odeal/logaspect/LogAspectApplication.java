package com.odeal.logaspect;

import com.odeal.logaspect.logger.controller.UniqueIDGenerator;
import com.odeal.logaspect.logger.controller.aspect.GenericControllerAspect;
import com.odeal.logaspect.logger.controller.filter.SpringLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LogAspectApplication {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public UniqueIDGenerator generator() {
		return new UniqueIDGenerator();
	}

	@Bean
	public SpringLoggingFilter loggingFilter() {
		return new SpringLoggingFilter(generator());
	}

    @Bean
    public GenericControllerAspect genericControllerAspect() {
        return new GenericControllerAspect();
    }

	public static void main(String[] args) {

		SpringApplication.run(LogAspectApplication.class, args);

	}

}
