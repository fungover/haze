package org.fungover.haze;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
	void wrongPasswordSholdReturnError() {
		auth.setPassword("12345");
		assertThat(auth.authenticate("123456")).isEqualTo("Ah ah ah, you didn't say the magic word.");

	}



}
