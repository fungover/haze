package org.fungover.haze;


import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class AuthTest {
	Auth auth = new Auth();

	@Test
	void passwordIsNotSetShouldReturnOK() {
		auth.setPassword("");
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
	void passwordSetByEnvironmentVariableShouldReturnOK() {
		assertThat(auth.authenticate("12345")).isEqualTo("+OK\\r\\n\n");
	}

	@Test
	void authenticateWithEmptyStringIfPasswordIsSet() {
		assertThat(auth.authenticate()).isEqualTo("Ah ah ah, you didn't say the magic word.");
	}
	@Test
	void passwordIsNotSetShouldReturnOKEvenIfPasswordSent() {
		auth.setPassword("");
		assertThat(auth.authenticate("1234")).isEqualTo("+OK\\r\\n\n");
	}


}
