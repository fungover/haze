package org.fungover.haze;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InitializeTest {

	Initialize initialize = new Initialize();

	@BeforeEach
	void setup() {
		initialize.clearCliOptions();
	}

	@Test
	void portSetByCLIWithDashPShouldReturn1234() {
		String[] args = {"-p", "1234"};
		initialize.importCliOptions(args);

		assertThat(initialize.setPort()).isEqualTo(1234);
	}

	@Test
	void portSetByEnvironmentVariableShouldReturn6380() {
		assertThat(initialize.setPort()).isEqualTo(6380);
	}

	@Test
	void portSetByCLIWithDashPortShouldReturn1233() {
		String[] args = {"--port", "1233"};
		initialize.importCliOptions(args);

		assertThat(initialize.setPort()).isEqualTo(1233);
	}
}
