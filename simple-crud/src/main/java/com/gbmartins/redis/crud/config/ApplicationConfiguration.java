package com.gbmartins.redis.crud.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;


@EnableAutoConfiguration
@Configuration
@EnableConfigurationProperties(value = { RedisSetup.class })
public class ApplicationConfiguration {

	private static final Logger LOG = LogManager.getLogger(ApplicationConfiguration.class);

	@Autowired
	private RedisSetup redisSetup;

	@Bean
	public PropertySource<?> yamlPropertySourceLoader() throws IOException {
		YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
		PropertySource<?> applicationYamlPropertySource = loader.load("application.yml",
				new ClassPathResource("application.yml"), "default");
		return applicationYamlPropertySource;
	}
	
	/*
	@Bean(name = "redisSetup")
	public RedisInstanceSetup redisInstanceSetup() {
		RedisInstanceSetup.createInstance(redisSetup.getHostname(), redisSetup.getPort(), redisSetup.getPassword());
		return RedisInstanceSetup.getInstance();
	}
	*/

	@PostConstruct
	public void init() {
		printProperties();
	}

	private void printProperties() {

		LOG.info("Property - Redis hostname        : " + redisSetup.getHostname());
		LOG.info("Property - Redis port            : " + redisSetup.getPort());
		LOG.info("Property - Redis pass            : " + (redisSetup.getPassword() != null ? "*****" : "<empty>"));
	}

}
