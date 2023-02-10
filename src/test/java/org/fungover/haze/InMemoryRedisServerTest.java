package org.fungover.haze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRedisServerTest {

	InMemoryRedisServer redisServer = new InMemoryRedisServer();


	@Test
	public void testSetWithValidKeyValuePair() {
		String result = redisServer.set("key", "value");
		assertEquals("+OK\r\n", result);
	}

	@Test
	public void testSetWithNullValue() {
		String result = redisServer.set("key", null);
		assertEquals("+OK\r\n", result);
	}

	@Test
	public void testGetWithValidKey() {
		redisServer.set("key", "value");
		String result = redisServer.get("key");
		assertEquals("$5\r\nvalue\r\n", result);
	}

	@Test
	public void testGetWithInvalidKey() {
		String result = redisServer.get("invalidKey");
		assertEquals("$-1\r\n", result);
	}

	@Test
	public void testGetWithNullKey() {
		String result = redisServer.get(null);
		assertEquals("$-1\r\n", result);
	}

	@Test
	public void testGetStringWithValidKey() {
		redisServer.set("key", "value");
		String result = redisServer.getString("key");
		assertEquals("$5\r\nvalue\r\n", result);
	}

	@Test
	public void testGetStringWithNullKey() {
		try {
			redisServer.getString(null);
			fail();
		} catch (NullPointerException npe) {

		}
	}
}
