package org.fungover.haze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedisServerTest {

	RedisServer redisServer = new RedisServer();


	@Test
	void testingSetAndGet(){
		var result = redisServer.set("key1", "value1");
		assertEquals("+OK\r\n", result);

		result = redisServer.get("key1");
		assertEquals("$6\r\nvalue1\r\n", result);
	}

	@Test
	void testGetForNonExistentKey() {
		var result = redisServer.get("nonExistentKey");

		assertEquals("$-1\r\n",result);
	}
}
