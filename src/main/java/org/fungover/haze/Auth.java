package org.fungover.haze;

public class Auth {
	private String password = System.getenv("PASSWORD");
    private static final String OK = "+OK\\r\\n\n";


	public void setPassword(String password) {
		this.password = password;
	}

	public String authenticate(String password) {
		if (passwordNotSet())
			return OK;

		if (this.password.equals(password))
			return OK;

		return "Ah ah ah, you didn't say the magic word.";
	}

	public String authenticate() {
		if (passwordNotSet())
			return OK;
		return "Ah ah ah, you didn't say the magic word.";
	}

	private boolean passwordNotSet() {
		return (this.password == null || this.password.isBlank());
	}

}
