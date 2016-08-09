package com.gbmartins.redis.crud.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gbmartins.redis.crud.AbstractTestBase;
import com.gbmartins.redis.dao.RedisOperations;

public class RedisOperationTest extends AbstractTestBase {

	@Autowired
	private RedisOperations operations;

	@Test
	public void testSaveObjectOperation() throws Exception {
		SimpleBean bean = new SimpleBean(1, "name");
		String key = "key1";

		operations.saveOrUpdateObject(key, bean);

		SimpleBean newBean = operations.getObject(key, SimpleBean.class);

		assertNotNull(newBean);
		assertEquals(bean.getId(), newBean.getId());
		assertEquals(bean.getName(), newBean.getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetObjectOperationIllegalArgumentExeception() throws Exception {
		SimpleBean bean = new SimpleBean(1, "name");
		String key = "key2";

		operations.saveOrUpdateObject(key, bean);

		operations.getObject(key, String.class);

		fail("Why am I here?");
	}

	@Test
	public void testSaveBulkObjectOperation() throws Exception {
		Map<String, SimpleBean> map = new HashMap<>();
		for (int i = 0; i < 30; i++) {
			map.put("key" + i, new SimpleBean(i, "name" + i));
		}

		operations.saveOrUpdateBulkObject(map);

		Iterator<Entry<String, SimpleBean>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, SimpleBean> pair = (Map.Entry<String, SimpleBean>) it.next();
			String key = pair.getKey();
			SimpleBean bean = pair.getValue();
			SimpleBean newBean = operations.getObject(key, SimpleBean.class);

			assertNotNull(newBean);
			assertEquals(bean.getId(), newBean.getId());
			assertEquals(bean.getName(), newBean.getName());
		}
	}
	
	@Test
	public void testGetNotFoundKey() throws Exception {
		SimpleBean bean = null;
		
		bean = operations.getObject("anyKey", SimpleBean.class);
		
		assertNull(bean);
	}

	
	private static class SimpleBean implements Serializable {
		private static final long serialVersionUID = -5023841794057529771L;
		private int id;
		private String name;

		public SimpleBean(int id, String name) {
			super();
			setId(id);
			setName(name);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
