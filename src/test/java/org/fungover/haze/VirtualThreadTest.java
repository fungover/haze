package org.fungover.haze;

import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadTest {

	HashMap<String, String> testDatabase = new HashMap<>();
	VirtualThread virtualThread = new VirtualThread(testDatabase);

	@Test
	void addToDatabase() {
		virtualThread.setDatabaseKey("testKey");
		virtualThread.setDatabaseValue("testValue");
		virtualThread.run();

		assertEquals("testValue", virtualThread.getDatabase().get("testKey"));

	}
	@Test
	void addToDatabaseWithOwnThread() {

		virtualThread.startNewVirtualThread("1", "1", testDatabase);
		virtualThread.startNewVirtualThread("2", "2", testDatabase);


		assertEquals("1", testDatabase.get("1"));


	}



}