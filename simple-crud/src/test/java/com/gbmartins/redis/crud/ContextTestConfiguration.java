package com.gbmartins.redis.crud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.gbmartins.redis.dao.RedisInstanceSetup;

@Configuration
public class ContextTestConfiguration {

	@Configuration
	@Profile({"test", "default"})
	@PropertySource(value = "classpath:redis-test-config.properties", ignoreResourceNotFound = true)
	static class ProfileTest {

		@Value("${redis-hostname}")
		private String hostname;
		
		@Value("${redis-port}")
		private Integer port;

		@Value("${redis-password}")
		private String password;
		
		@Bean(name="redisSetup")
		public RedisInstanceSetup redisInstanceSetup() {
			RedisInstanceSetup.createInstance(hostname, port, password);
			return RedisInstanceSetup.getInstance();
		}
		
		//To resolve ${} in @Value
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}
	
	@Configuration
	@Profile({"ci"})
	@PropertySource(value = "classpath:redis-ci-config.properties", ignoreResourceNotFound = true)
	static class ProfileCI {

		@Value("${redis-hostname}")
		private String hostname;
		
		@Value("${redis-port}")
		private Integer port;

		@Value("${redis-password}")
		private String password;
		
		@Bean(name="redisSetup")
		public RedisInstanceSetup redisInstanceSetup() {
			RedisInstanceSetup.createInstance(hostname, port, password);
			return RedisInstanceSetup.getInstance();
		}
		
		//To resolve ${} in @Value
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigInCI() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}
	
	

}