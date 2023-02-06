package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadTest {

	HashMap<String, String> testDatabase = new HashMap<>();
	VirtualThread virtualThread = new VirtualThread(testDatabase);


	@Test
	void ThreadStartingShouldReturnThreadNames() {

		virtualThread.startNewVirtualThread("1", "1", testDatabase);
		virtualThread.startNewVirtualThread("2", "2", testDatabase);
		System.out.println(testDatabase);
		assertEquals("1", testDatabase.get("1"));


	}


}