package org.fungover.haze;

public class Auth {
	private String password = System.getenv("password");


	public void setPassword(String password) {
		this.password = password;
	}

	public String authenticate(String password) {
		if (passwordNotSet())
			return "+OK\\r\\n\n";

		if (this.password.equals(password))
			return "+OK\\r\\n\n";

		return "Ah ah ah, you didn't say the magic word.";
	}
	public String authenticate() {
			return "+OK\\r\\n\n";
	}

	private boolean passwordNotSet() {
		return this.password.isBlank();
	}

}
