package com.gbmartins.redis.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gbmartins.redis.dao.RedisPoolConnection;

import redis.clients.jedis.Jedis;

public class RedisConfigurationTest extends AbstractTestBase {

	@Autowired
	private RedisPoolConnection redisFactoryBean;

	@Test
	public void testRedisPoolConnection() {
		Jedis jedis = redisFactoryBean.getResource();
		String key = "key";

		jedis.set(key, "value");

		String value = jedis.get(key);

		assertNotNull(value);
		assertEquals("value", value);
	}

	@Test
	public void testMultithreadConnection() throws InterruptedException {
		Object pauseLock = new Object();
		int numberThreads = 10;
		List<Thread> threads = new ArrayList<Thread>();
		ValueSum valueSum = new ValueSum();

		int sum = 0;
		for (int i = 0; i < numberThreads; i++) {
			sum += i;
			ThreadConnection thread = new ThreadConnection(String.valueOf(i), i, pauseLock, redisFactoryBean, valueSum);
			Thread t = new Thread(thread);
			t.start();
			threads.add(t);
		}

		for (Thread t : threads) {
			if (t.isAlive())
				t.join();
		}

		assertTrue(sum + " == " + valueSum.getValue(), sum == valueSum.getValue());

	}

	private static class ValueSum {
		private int value;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}

	private static class ThreadConnection implements Runnable {

		private String key;
		private int value;
		private Object pauseLock;
		private RedisPoolConnection redisFactoryBean;
		private ValueSum sum;

		public ThreadConnection(String key, int value, Object pauseLock, RedisPoolConnection redisFactoryBean,
				ValueSum sum) {
			super();
			this.key = key;
			this.value = value;
			this.pauseLock = pauseLock;
			this.sum = sum;
			this.redisFactoryBean = redisFactoryBean;
		}

		@Override
		public void run() {

			try {

				try (Jedis jedis = redisFactoryBean.getResource()) {

					jedis.set(key, String.valueOf(this.value));

					int myValue = Integer.parseInt(jedis.get(key));

					synchronized (pauseLock) {
						sum.setValue(sum.getValue() + myValue);
					}
				}
			} catch (Exception ex) {
				// die alone
			}
		}

	}
}