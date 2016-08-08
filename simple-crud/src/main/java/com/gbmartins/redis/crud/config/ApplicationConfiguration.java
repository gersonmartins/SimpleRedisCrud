/*
 * The MIT License (MIT)
 * Copyright © 2015-2016 Gerson B. Martins (gbmartins.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the “Software”), to deal in the Software without 
 * restriction, including without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gbmartins.redis.crud.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import com.gbmartins.redis.dao.RedisInstanceSetup;


/**
 * The Class ApplicationConfiguration.
 */
@EnableAutoConfiguration
@Configuration
@EnableConfigurationProperties(value = { RedisSetup.class })
public class ApplicationConfiguration {

	/** The redis setup. */
	@Autowired
	private RedisSetup redisSetup;

	/**
	 * Yaml property source loader.
	 *
	 * @return the property source
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Bean
	public PropertySource<?> yamlPropertySourceLoader() throws IOException {
		YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
		PropertySource<?> applicationYamlPropertySource = loader.load("application.yml",
				new ClassPathResource("application.yml"), "default");
		return applicationYamlPropertySource;
	}
	
	/**
	 * Redis instance setup.
	 *
	 * @return the redis instance setup
	 */
	@Bean(name = "redisSetup")
	public RedisInstanceSetup redisInstanceSetup() {
		RedisInstanceSetup.createInstance(redisSetup.getHostname(), redisSetup.getPort(), redisSetup.getPassword());
		return RedisInstanceSetup.getInstance();
	}

}
