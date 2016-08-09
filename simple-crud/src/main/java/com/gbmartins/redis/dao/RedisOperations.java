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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * The Class RedisOperations.
 */
@Component
public class RedisOperations {

	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(RedisOperations.class);

	/** The redis factory. */
	private RedisPoolConnection redisFactory;

	/**
	 * Instantiates a new redis operations.
	 *
	 * @param redisFactory
	 *            the redis factory
	 */
	@Autowired
	public RedisOperations(RedisPoolConnection redisFactory) {
		super();
		this.redisFactory = redisFactory;
	}


	/**
	 * Save or update object.
	 *
	 * @param <T> the generic type
	 * @param key the key
	 * @param object the object
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T extends Serializable> String saveOrUpdateObject(String key, T object) throws IOException {

		try {

			byte[] bytes = serializeObject(object);
			String result = null;

			try (Jedis jedis = redisFactory.getResource()) {
				result = jedis.set(key.getBytes(), bytes);
			}
			
			return result;

		} catch (IOException ex) {
			LOG.error("I/O Error when trying to save object", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("Unknown Error when trying to save object", ex);
			throw ex;
		}

	}
		

	/**
	 * Gets the object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param type
	 *            the type
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	public <T extends Serializable> T getObject(String key, Class<T> type) throws Exception {

		try {

			byte[] bytes = null;

			try (Jedis jedis = redisFactory.getResource()) {
				bytes = jedis.get(key.getBytes());
			}
			
			if (bytes == null) {
				return null;
			}

			return deserializeObject(bytes, type);
		} catch (IOException | ClassNotFoundException cne) {
			LOG.error("I/O Error or Class Not Found when trying to get object", cne);
			throw cne;
		} catch (IllegalArgumentException iae) {
			LOG.error("Illegal Argument when trying to get object", iae);
			throw iae;
		} catch (Exception ex) {
			LOG.error("Unknown Error when trying to save object", ex);
			throw ex;
		}

	}

	/**
	 * Save or update bulk object.
	 *
	 * @param <T> the generic type
	 * @param bulk the bulk
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T extends Serializable> void saveOrUpdateBulkObject(Map<String, T> bulk) throws IOException {

		if (bulk == null) {
			throw new RuntimeException("bulk cannot be null or empty");
		}

		try {

			try (Jedis jedis = redisFactory.getResource()) {
				Pipeline p = jedis.pipelined();

				Iterator<Entry<String, T>> it = bulk.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, T> pair = (Map.Entry<String, T>) it.next();
					byte[] key = pair.getKey().getBytes();
					byte[] value = serializeObject(pair.getValue());
					p.set(key, value);
				}
				p.sync();
			}

		} catch (IOException ex) {
			LOG.error("I/O Error when trying to save bulk object", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("Error when trying to save bulk object", ex);
			throw ex;
		}

	}

	/**
	 * Gets the list object.
	 *
	 * @param <T>            the generic type
	 * @param keys            the keys
	 * @param type            the type
	 * @return the list object
	 * @throws Exception the exception
	 */
	public <T extends Serializable> List<T> getListObject(List<String> keys, Class<T> type) throws Exception {
		if (keys == null || keys.isEmpty()) {
			throw new RuntimeException("keys cannot be null or empty");
		}

		List<T> result = new ArrayList<>(keys.size());
		List<Object> responses = new ArrayList<>(keys.size());

		try {

			try (Jedis jedis = redisFactory.getResource()) {
				Pipeline p = jedis.pipelined();

				for (String k : keys) {
					p.get(k.getBytes());
				}
				responses = p.syncAndReturnAll();
			}

			for (Object o : responses) {
				if (o != null) {
					result.add(deserializeObject((byte[]) o, type));
				}
			}

			return result;

		} catch (IOException ex) {
			LOG.error("I/O Error when trying to save bulk object", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("Error when trying to save bulk object", ex);
			throw ex;
		}
	}

	/**
	 * Delete keys.
	 *
	 * @param keys
	 *            the keys
	 * @return the long
	 */
	public long deleteKeys(String... keys) {
		long numberDeleted = 0;
		try (Jedis jedis = redisFactory.getResource()) {
			numberDeleted = jedis.del(keys);
		}
		return numberDeleted;
	}
	

	/**
	 * Script load.
	 *
	 * @param script
	 *            the script
	 * @return the string
	 */
	public String scriptLoad(String script) {
		String sha = null;
		try (Jedis jedis = redisFactory.getResource()) {
			sha = jedis.scriptLoad(script);
		}
		return sha;
	}

	/**
	 * Serialize object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param object
	 *            the object
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private <T extends Serializable> byte[] serializeObject(T object) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;

		try {

			out = new ObjectOutputStream(bos);
			out.writeObject(object);

			byte[] bytes = bos.toByteArray();

			return bytes;
		} catch (Exception ex) {
			LOG.error("Unknown Error when trying to serialize Object", ex);
			throw ex;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception eex) {
				// ignore
			}

			try {
				bos.close();
			} catch (Exception eeex) {
				/// ignore
			}
		}

	}

	/**
	 * Deserialize object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param bytes
	 *            the bytes
	 * @param type
	 *            the type
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private <T extends Serializable> T deserializeObject(byte[] bytes, Class<T> type) throws Exception {

		ByteArrayInputStream bis = null;
		ObjectInput in = null;

		try {
			bis = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			if (type.isInstance(o)) {
				return (T) o;
			} else {
				throw new IllegalArgumentException("Object is not " + type.getCanonicalName());
			}
		} catch (Exception ex) {
			LOG.error("Error when trying to deserializeObject", ex);
			throw ex;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception eex) {
				// ignore
			}

			try {
				bis.close();
			} catch (Exception eeex) {
				/// ignore
			}
		}

	}

}
