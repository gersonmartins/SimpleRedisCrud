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

/**
 * The Class RedisInstanceSetup.
 */
public class RedisInstanceSetup {
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(RedisInstanceSetup.class);

	/** The hostname. */
	private String hostname;

	/** The port. */
	private int port;

	/** The password. */
	private String password;

	/** The instance. */
	private static RedisInstanceSetup instance;

	/**
	 * Creates the instance.
	 *
	 * @param hostname
	 *            the hostname
	 * @param port
	 *            the port
	 * @param password
	 *            the password
	 * @return the redis instance setup
	 */
	public static RedisInstanceSetup createInstance(String hostname, int port, String password) {
		if (instance == null) {
			LOG.debug("New Setup for Redis has been created");
			instance = new RedisInstanceSetup(hostname, port, password);
		} else {
			LOG.debug("Current Setup for Redis has been changed");
			instance.hostname = hostname;
			instance.password = password;
			instance.port = port;
		}

		return instance;
	}

	/**
	 * Instantiates a new redis instance setup.
	 *
	 * @param hostname
	 *            the hostname
	 * @param port
	 *            the port
	 * @param password
	 *            the password
	 */
	private RedisInstanceSetup(String hostname, int port, String password) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.password = password;
	}

	/**
	 * Gets the single instance of RedisInstanceSetup.
	 *
	 * @return single instance of RedisInstanceSetup
	 */
	public static RedisInstanceSetup getInstance() {
		if (instance == null) {
			throw new RuntimeException("RedisInstanceSetup wasn't properly initialized");
		}

		return instance;
	}

	/**
	 * Gets the hostname.
	 *
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

}