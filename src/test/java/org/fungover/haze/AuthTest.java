package org.fungover.haze;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static com.github.stefanbirkner.systemlambda.SystemLambda.*;


class AuthTest {
	Auth auth = new Auth();

	@Test
	void passwordIsNotSetShouldReturnOK() {
		assertThat(auth.authenticate()).isEqualTo("+OK\\r\\n\n");
	}

	@Test
	void passwordIsSetShouldReturnOK() {
		auth.setPassword("123456");
		assertThat(auth.authenticate("123456")).isEqualTo("+OK\\r\\n\n");
	}

	@Test
	void wrongPasswordShouldReturnError() {
		auth.setPassword("12345");
		assertThat(auth.authenticate("123456")).isEqualTo("Ah ah ah, you didn't say the magic word.");

	}

	@Test
	@SetEnvironmentVariable(key = "PASSWORD", value = "123456")
	void passwordSetByEnvironmentVariableShouldReturnOK() throws Exception {
		Auth auth1 = new Auth();

		withEnvironmentVariable("PASSWORD", "123456")
				.execute(() -> System.getenv("PASSWORD"));
		assertThat(auth1.authenticate("123456")).isEqualTo("+OK\\r\\n\n");
	}

	@Test
	@SetEnvironmentVariable(key = "some variable",value = "new value")
	void test() {
		assertThat(System.getenv("some variable")).
				isEqualTo("new value");
	}



}
