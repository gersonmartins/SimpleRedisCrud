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
package com.gbmartins.redis.crud;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;

import com.gbmartins.redis.crud.config.ApplicationConfiguration;
import com.gbmartins.redis.crud.config.RedisApplicationConfiguration;
import com.google.common.collect.Sets;

/**
 * The Class SimpleCrud.
 */
public class SimpleCrud {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.setSources(Sets.newHashSet(ApplicationConfiguration.class, RedisApplicationConfiguration.class));
		app.setWebEnvironment(false);
		app.run(args);

	}

}
