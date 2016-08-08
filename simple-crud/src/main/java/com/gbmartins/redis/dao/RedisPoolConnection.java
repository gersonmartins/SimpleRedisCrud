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
package com.gbmartins.redis.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * The Class RedisPoolConnection.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RedisPoolConnection {

	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(RedisPoolConnection.class);

	/** The pool. */
	private JedisPool pool;

	/**
	 * Instantiates a new redis factory.
	 *
	 * @param redisInstanceSetup the redis instance setup
	 */
	@Autowired
	public RedisPoolConnection(@Qualifier("redisSetup") RedisInstanceSetup redisInstanceSetup) {
		LOG.info("Redis Factory initialized with Hostname: {}, Port {}, Password: {}", redisInstanceSetup.getHostname(),
				redisInstanceSetup.getPort(),
				Strings.isNullOrEmpty(redisInstanceSetup.getPassword()) ? "<empty>" : "*******");
		JedisPoolConfig config = new JedisPoolConfig();
		pool = new JedisPool(config, redisInstanceSetup.getHostname(), redisInstanceSetup.getPort(), 0,
				redisInstanceSetup.getPassword());
	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public Jedis getResource() {
		return pool.getResource();
	}
}
